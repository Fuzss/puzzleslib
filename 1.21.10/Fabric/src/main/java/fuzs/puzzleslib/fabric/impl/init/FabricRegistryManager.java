package fuzs.puzzleslib.fabric.impl.init;

import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.registry.MenuSupplierWithData;
import fuzs.puzzleslib.impl.init.DirectReferenceHolder;
import fuzs.puzzleslib.impl.init.LazyHolder;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class FabricRegistryManager extends RegistryManagerImpl {
    private static final ResourceKey<Registry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS_REGISTRY_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.withDefaultNamespace("entity_data_serializers"));

    private boolean isFrozen;

    public FabricRegistryManager(String modId) {
        super(modId);
    }

    @Override
    public <T> Holder.Reference<T> registerLazily(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        this.isWritableOrThrow();
        Registry<T> registry = LookupHelper.getRegistry(registryKey).orElseThrow();
        ResourceKey<T> resourceKey = this.makeResourceKey(registryKey, path);
        return new LazyHolder<>(registryKey, resourceKey, () -> {
            Holder.Reference<T> holder = registry.getOrThrow(resourceKey);
            if (!holder.isBound()) {
                T value = registry.getValue(resourceKey);
                Objects.requireNonNull(value, "value is null");
                holder.bindValue(value);
            }

            return holder;
        });
    }

    @Override
    protected <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
        T value = supplier.get();
        Objects.requireNonNull(value, "value is null");
        Registry<T> registry = LookupHelper.getRegistry(registryKey).orElseThrow();
        Holder.Reference<T> holder;
        if (skipRegistration) {
            holder = registry.getOrThrow(this.makeResourceKey(registryKey, path));
        } else {
            holder = Registry.registerForHolder(registry, this.makeKey(path), value);
        }

        // bind the value immediately, so we can use it
        // Fabric Api should already do this for us, but better safe than sorry ¯\_(ツ)_/¯
        if (!holder.isBound()) {
            holder.bindValue(value);
        }

        return holder;
    }

    @Override
    protected CreativeModeTab.Builder getCreativeModeTabBuilder(boolean withSearchBar) {
        return FabricItemGroup.builder();
    }

    @Override
    public <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<Set<Block>> validBlocks) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE,
                path,
                () -> {
                    return FabricBlockEntityTypeBuilder.create(blockEntityFactory::apply,
                            validBlocks.get().toArray(Block[]::new)).build();
                });
    }

    @Override
    public <T extends AbstractContainerMenu, S> Holder.Reference<MenuType<T>> registerMenuType(String path, MenuSupplierWithData<T, S> menuSupplier, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU,
                path,
                () -> new ExtendedScreenHandlerType<>(menuSupplier::create, streamCodec));
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
    public <T> Holder.Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String path, Supplier<EntityDataSerializer<T>> entityDataSerializerSupplier) {
        ResourceKey<EntityDataSerializer<T>> resourceKey = this.makeResourceKey(ENTITY_DATA_SERIALIZERS_REGISTRY_KEY,
                path);
        Holder.Reference<EntityDataSerializer<T>> holder = new DirectReferenceHolder<>(resourceKey,
                entityDataSerializerSupplier.get());
        FabricTrackedDataRegistry.register(holder.key().location(), holder.value());
        return holder;
    }

    @Override
    public <T> void prepareTag(ResourceKey<? extends Registry<? super T>> registryKey, TagKey<T> tagKey) {
        this.isWritableOrThrow();
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(tagKey, "tag key is null");
        Registry<T> registry = LookupHelper.getRegistry(registryKey).orElseThrow();
        BuiltInRegistries.acquireBootstrapRegistrationLookup(registry).getOrThrow(tagKey);
    }

    @Override
    public void freeze() {
        this.isWritableOrThrow();
        this.isFrozen = true;
    }

    @Override
    public boolean isFrozen() {
        return this.isFrozen;
    }
}
