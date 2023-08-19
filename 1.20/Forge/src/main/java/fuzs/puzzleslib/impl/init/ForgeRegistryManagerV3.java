package fuzs.puzzleslib.impl.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ObjectHolderRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class ForgeRegistryManagerV3 extends RegistryManagerV3Impl {
    private final IEventBus eventBus;
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = Maps.newIdentityHashMap();

    public ForgeRegistryManagerV3(String modId) {
        super(modId);
        this.eventBus = ModContainerHelper.findModEventBus(modId).orElseThrow();
    }

    @Override
    public <T> Holder.Reference<T> getHolder(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        Registry<T> registry = findRegistry(registryKey);
        ResourceKey<T> resourceKey = this.makeResourceKey(registryKey, path);
        // Forge cannot use the holder from the registry as it does not exist yet
        // an alternative would be implementing a forwarding holder instance, but that would require returning Holder over Holder.Reference everywhere
        // we try to stick to Holder.Reference to have easy access to the resource key and thereby resource location
        Holder.Reference<T> holder = Holder.Reference.createStandAlone(registry.asLookup(), resourceKey);
        ObjectHolderRegistry.addHandler(predicate -> {
            if (predicate.test(registryKey.location()) && !holder.isBound()) {
                T value = registry.get(resourceKey);
                Objects.requireNonNull(value, "value is null");
                holder.bindValue(value);
            }
        });
        // since we do not use the actual holder kept in the registry, tags need to be updated manually,
        // so we replicate those calls which is easy since Forge has events everywhere right after tags update
        // this possibly might break with other mods interfering that rely on calling this themselves,
        // in such a case switch to the implementation outlined above
        MinecraftForge.EVENT_BUS.addListener((final TagsUpdatedEvent evt) -> {
            holder.bindTags(registry.getHolder(resourceKey).stream().flatMap(Holder.Reference::tags).toList());
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            MinecraftForge.EVENT_BUS.addListener((final ClientPlayerNetworkEvent.LoggingIn evt) -> {
                if (evt.getConnection().isMemoryConnection()) return;
                holder.bindTags(Set.of());
            });
        }
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Holder.Reference<T> _register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        DeferredRegister<T> register = (DeferredRegister<T>) this.registers.computeIfAbsent(registryKey, $ -> {
            DeferredRegister<T> deferredRegister = DeferredRegister.create((ResourceKey<? extends Registry<T>>) registryKey, this.modId);
            deferredRegister.register(this.eventBus);
            return deferredRegister;
        });
        register.register(path, () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value is null");
            return value;
        });
        return this.getHolder(registryKey, path);
    }

    @Override
    public Holder.Reference<Item> registerSpawnEggItem(Holder.Reference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.key().location().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(entityTypeReference, backgroundColor, highlightColor, itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> new MenuType<>((IContainerFactory<T>) (containerId, inventory, data) -> entry.get().create(containerId, inventory, data), FeatureFlags.DEFAULT_FLAGS));
    }

    @Override
    public Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE, path, () -> new PoiType(matchingStates.get(), maxTickets, validRange));
    }
}
