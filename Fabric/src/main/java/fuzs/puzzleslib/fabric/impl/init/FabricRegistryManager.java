package fuzs.puzzleslib.fabric.impl.init;

import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.init.v3.registry.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.puzzleslib.api.network.v3.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.impl.init.DirectReferenceHolder;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.impl.item.CreativeModeTabHelper;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class FabricRegistryManager extends RegistryManagerImpl {

    public FabricRegistryManager(String modId) {
        super(modId);
    }

    @Override
    protected <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
        T value = supplier.get();
        Objects.requireNonNull(value, "value is null");
        Registry<T> registry = RegistryHelper.findBuiltInRegistry(registryKey);
        Holder.Reference<T> holder;
        if (skipRegistration) {
            holder = registry.getOrThrow(this.makeResourceKey(registryKey, path));
        } else {
            holder = Registry.registerForHolder(registry, this.makeKey(path), value);
        }
        // bind the value immediately, so we can use it
        // Fabric Api should already do this for us, but better safe than sorry ¯\_(ツ)_/¯
        if (!holder.isBound()) holder.bindValue(value);
        return holder;
    }

    @Override
    public Holder.Reference<CreativeModeTab> registerCreativeModeTab(Supplier<ItemStack> iconSupplier) {
        return this.registerCreativeModeTab("main", (CreativeModeTab.Builder builder) -> {
            builder.icon(iconSupplier);
            CreativeModeTab.DisplayItemsGenerator displayItems = CreativeModeTabHelper.getDisplayItems(this.modId);
            builder.displayItems(displayItems);
        });
    }

    @Override
    protected CreativeModeTab.Builder getCreativeModeTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, BiFunction<BlockPos, BlockState, T> factory, Supplier<Set<Block>> validBlocks) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE,
                path,
                () -> {
                    return FabricBlockEntityTypeBuilder.create(factory::apply, validBlocks.get().toArray(Block[]::new))
                            .build();
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU,
                path,
                () -> new ExtendedScreenHandlerType<>(entry.get()::create,
                        ExtraStreamCodecs.REGISTRY_FRIENDLY_BYTE_BUF));
    }

    @Override
    public Holder.Reference<PoiType> registerPoiType(String path, int maxTickets, int validRange, Supplier<Set<BlockState>> matchingBlockStates) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE,
                path,
                () -> PointOfInterestHelper.register(this.makeKey(path),
                        maxTickets,
                        validRange,
                        matchingBlockStates.get()),
                true);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> Holder.Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(String path, Class<? extends A> argumentClass, ArgumentTypeInfo<A, T> argumentTypeInfo) {
        return this.register(Registries.COMMAND_ARGUMENT_TYPE, path, () -> {
            ArgumentTypeRegistry.registerArgumentType(this.makeKey(path), argumentClass, argumentTypeInfo);
            return argumentTypeInfo;
        }, true);
    }

    @Override
    public <T> Holder.Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String path, Supplier<EntityDataSerializer<T>> entry) {
        ResourceKey<Registry<EntityDataSerializer<?>>> registryKey = ResourceKey.createRegistryKey(
                ResourceLocationHelper.withDefaultNamespace("entity_data_serializers"));
        EntityDataSerializer<T> serializer = entry.get();
        EntityDataSerializers.registerSerializer(serializer);
        return new DirectReferenceHolder<>(this.makeResourceKey(registryKey, path), serializer);
    }
}
