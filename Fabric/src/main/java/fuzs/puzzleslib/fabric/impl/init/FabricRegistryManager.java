package fuzs.puzzleslib.fabric.impl.init;

import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.init.v3.registry.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.registry.MenuSupplierWithData;
import fuzs.puzzleslib.api.network.v3.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.impl.init.DirectReferenceHolder;
import fuzs.puzzleslib.impl.init.LazyHolder;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class FabricRegistryManager extends RegistryManagerImpl {
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
            Holder.Reference<T> holder = registry.getHolderOrThrow(resourceKey);
            if (!holder.isBound()) {
                T value = registry.get(resourceKey);
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
            holder = registry.getHolderOrThrow(this.makeResourceKey(registryKey, path));
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
    public Holder.Reference<Item> registerLegacySpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor) {
        return this.registerItem(entityTypeReference.unwrapKey().orElseThrow().location().getPath() + "_spawn_egg",
                (Item.Properties itemProperties) -> new SpawnEggItem(entityTypeReference.value(),
                        backgroundColor,
                        highlightColor,
                        itemProperties));
    }

    @Override
    public <T extends AbstractContainerMenu, S> Holder.Reference<MenuType<T>> registerMenuType(String path, MenuSupplierWithData<T, S> menuSupplier, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU,
                path,
                () -> new ExtendedScreenHandlerType<>(menuSupplier::create, streamCodec));
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

    @Override
    public <T> void prepareTag(ResourceKey<? extends Registry<? super T>> registryKey, TagKey<T> tagKey) {
        this.isWritableOrThrow();
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(tagKey, "tag key is null");
        Registry<T> registry = LookupHelper.getRegistry(registryKey).orElseThrow();
        ((WritableRegistry<T>) registry).createRegistrationLookup().getOrThrow(tagKey);
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
