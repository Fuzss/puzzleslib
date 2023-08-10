package fuzs.puzzleslib.impl.init;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v2.builder.PoiTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * <p>this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * <p>originally heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
public class FabricRegistryManager implements RegistryManager {
    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * defer registration for this manager until {@link #applyRegistration()} is called
     */
    public final boolean deferred;
    /**
     * internal storage for collecting and registering registry entries
     */
    private final Multimap<ResourceKey<? extends Registry<?>>, Runnable> registryToFactory = ArrayListMultimap.create();
    /**
     * mod loader sto register the next entry to, null by default for registering to any
     */
    @Nullable
    private Set<ModLoader> allowedModLoaders;

    public FabricRegistryManager(String modId, boolean deferred) {
        this.namespace = modId;
        this.deferred = deferred;
    }

    @Override
    public String namespace() {
        return this.namespace;
    }

    @Override
    public RegistryManager whenOn(ModLoader... allowedModLoaders) {
        Preconditions.checkPositionIndex(0, allowedModLoaders.length - 1, "mod loaders is empty");
        this.allowedModLoaders = Sets.immutableEnumSet(Arrays.asList(allowedModLoaders));
        return this;
    }

    @Override
    public void applyRegistration() {
        if (!this.deferred || this.registryToFactory.isEmpty()) throw new IllegalStateException("No registry entries available for deferred registration");
        // follow the same order as Forge: blocks, items, everything else
        // this will run into issues for spawn eggs, as the eggs will be registered before the entity type is created -> we'll deal with this when it's required by a mod
        this.registryToFactory.get(Registry.BLOCK_REGISTRY).forEach(Runnable::run);
        this.registryToFactory.get(Registry.ITEM_REGISTRY).forEach(Runnable::run);
        for (Map.Entry<ResourceKey<? extends Registry<?>>, Collection<Runnable>> entry : this.registryToFactory.asMap().entrySet()) {
            if (entry.getKey() != Registry.BLOCK_REGISTRY && entry.getKey() != Registry.ITEM_REGISTRY) entry.getValue().forEach(Runnable::run);
        }
    }

    @Override
    public <T> RegistryReference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        Set<ModLoader> allowedModLoaders = this.allowedModLoaders;
        this.allowedModLoaders = null;
        if (!this.deferred) {
            return this.actuallyRegister(registryKey, path, supplier, allowedModLoaders);
        } else {
            this.registryToFactory.put(registryKey, () -> this.actuallyRegister(registryKey, path, supplier, allowedModLoaders));
            return this.placeholder(registryKey, path);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RegistryReference<T> actuallyRegister(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, @Nullable Set<ModLoader> allowedModLoaders) {
        if (allowedModLoaders != null && !allowedModLoaders.contains(ModLoader.FABRIC)) {
            return this.placeholder(registryKey, path);
        }
        T value = supplier.get();
        Registry<? super T> registry = (Registry<? super T>) Registry.REGISTRY.get(registryKey.location());
        Objects.requireNonNull(value, "Can't register null value");
        Objects.requireNonNull(registry, "Registry %s not found".formatted(registryKey));
        ResourceLocation key = this.makeKey(path);
        Registry.register(registry, key, value);
        return new FabricRegistryReference<>(value, key, registry);
    }

    @Override
    public RegistryReference<Item> registerSpawnEggItem(RegistryReference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.getResourceLocation().getPath() + "_spawn_egg", () -> new SpawnEggItem(entityTypeReference.get(), backgroundColor, highlightColor, itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registry.MENU_REGISTRY, path, () -> new ExtendedScreenHandlerType<>(entry.get()::create));
    }

    @Override
    public RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<PoiTypeBuilder> entry) {
        PoiTypeBuilder builder = entry.get();
        ResourceLocation key = this.makeKey(path);
        PoiType value = PointOfInterestHelper.register(key, builder.ticketCount(), builder.searchDistance(), builder.blocks());
        return new FabricRegistryReference<>(value, key, Registry.POINT_OF_INTEREST_TYPE);
    }
}
