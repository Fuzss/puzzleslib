package fuzs.puzzleslib.neoforge.impl.init;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.init.v3.registry.ExtendedMenuSupplier;
import fuzs.puzzleslib.impl.init.LazyHolder;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class NeoForgeRegistryManager extends RegistryManagerImpl {
    @Nullable
    private final IEventBus eventBus;
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = Maps.newIdentityHashMap();

    public NeoForgeRegistryManager(String modId) {
        super(modId);
        this.eventBus = NeoForgeModContainerHelper.getOptionalModEventBus(modId).orElse(null);
    }

    @Override
    public <T> Holder.Reference<T> registerLazily(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        return new LazyHolder<>(registryKey, DeferredHolder.create(this.makeResourceKey(registryKey, path)));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
        Preconditions.checkState(!skipRegistration, "Skipping registration is not supported on NeoForge");
        DeferredRegister<T> registrar = (DeferredRegister<T>) this.registers.computeIfAbsent(registryKey, $ -> {
            DeferredRegister<T> deferredRegister = DeferredRegister.create((ResourceKey<? extends Registry<T>>) registryKey,
                    this.modId);
            Objects.requireNonNull(this.eventBus, "mod event bus is null for " + this.modId);
            deferredRegister.register(this.eventBus);
            return deferredRegister;
        });
        return new LazyHolder<>(registryKey, registrar.register(path, () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value is null");
            return value;
        }));
    }

    @Override
    protected CreativeModeTab.Builder getCreativeModeTabBuilder(boolean withSearchBar) {
        return withSearchBar ? CreativeModeTab.builder().withSearchBar() : CreativeModeTab.builder();
    }

    @Override
    public Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.unwrapKey().orElseThrow().location().getPath() + "_spawn_egg",
                () -> new DeferredSpawnEggItem(entityTypeReference::value,
                        backgroundColor,
                        highlightColor,
                        itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU,
                path,
                () -> IMenuTypeExtension.create(entry.get()::create));
    }

    @Override
    public Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE,
                path,
                () -> new PoiType(matchingStates.get(), maxTickets, validRange));
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> Holder.Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(String path, Class<? extends A> argumentClass, ArgumentTypeInfo<A, T> argumentTypeInfo) {
        return this.register(Registries.COMMAND_ARGUMENT_TYPE, path, () -> {
            ArgumentTypeInfos.registerByClass((Class<A>) argumentClass, argumentTypeInfo);
            return argumentTypeInfo;
        });
    }

    @Override
    public <T> Holder.Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String path, Supplier<EntityDataSerializer<T>> entry) {
        return this.register(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, path, entry);
    }
}
