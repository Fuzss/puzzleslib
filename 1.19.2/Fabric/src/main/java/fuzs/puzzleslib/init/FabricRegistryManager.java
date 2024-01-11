package fuzs.puzzleslib.init;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.core.ModLoader;
import fuzs.puzzleslib.init.builder.ExtendedModMenuSupplier;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import fuzs.puzzleslib.init.builder.ModMenuSupplier;
import fuzs.puzzleslib.init.builder.ModPoiTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * <p>this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * <p>originally heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
public class FabricRegistryManager implements RegistryManager {
    /**
     * registry data is stored for each mod separately so when registry events are fired every mod is responsible for registering their own stuff
     * this is important so that entries are registered for the proper namespace
     */
    private static final Map<String, FabricRegistryManager> MOD_TO_REGISTRY = Maps.newConcurrentMap();

    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * defer registration for this manager until {@link #applyRegistration()} is called
     */
    private final boolean deferred;
    /**
     * internal storage for collecting and registering registry entries
     */
    private final Multimap<ResourceKey<? extends Registry<?>>, Runnable> registryToFactory = ArrayListMultimap.create();
    /**
     * mod loader sto register the next entry to, null by default for registering to any
     */
    @Nullable
    private Set<ModLoader> allowedModLoaders;

    /**
     * private constructor
     *
     * @param modId         namespace for this instance
     * @param deferred      defer registration for this manager until {@link #applyRegistration()} is called
     */
    private FabricRegistryManager(String modId, boolean deferred) {
        this.namespace = modId;
        this.deferred = deferred;
    }

    @Override
    public String namespace() {
        return this.namespace;
    }

    @Override
    public RegistryManager whenOn(ModLoader... allowedModLoaders) {
        if (allowedModLoaders.length == 0) throw new IllegalArgumentException("Must provide at least one mod loader to register on");
        this.allowedModLoaders = ImmutableSet.copyOf(allowedModLoaders);
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
        Set<ModLoader> modLoaders = this.allowedModLoaders;
        this.allowedModLoaders = null;
        if (!this.deferred) {
            return this.actuallyRegister(registryKey, path, supplier, modLoaders);
        } else {
            this.registryToFactory.put(registryKey, () -> this.actuallyRegister(registryKey, path, supplier, modLoaders));
            return this.placeholder(registryKey, path);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RegistryReference<T> actuallyRegister(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, @Nullable Set<ModLoader> modLoaders) {
        if (modLoaders != null && !modLoaders.contains(ModLoader.FABRIC)) {
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T extends BlockEntity> RegistryReference<BlockEntityType<T>> registerBlockEntityTypeBuilder(String path, Supplier<ModBlockEntityTypeBuilder<T>> entry) {
        return this.registerBlockEntityType(path, () -> {
            ModBlockEntityTypeBuilder<T> builder = entry.get();
            return BlockEntityType.Builder.of(builder.factory()::create, builder.blocks()).build(null);
        });
    }

    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerMenuTypeSupplier(String path, Supplier<ModMenuSupplier<T>> entry) {
        return this.registerMenuType(path, () -> new MenuType<>(entry.get()::create));
    }

    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerExtendedMenuTypeSupplier(String path, Supplier<ExtendedModMenuSupplier<T>> entry) {
        return this.registerMenuType(path, () -> new ExtendedScreenHandlerType<>(entry.get()::create));
    }

    @Override
    public RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<ModPoiTypeBuilder> entry) {
        ModPoiTypeBuilder builder = entry.get();
        ResourceLocation key = this.makeKey(path);
        PoiType value = PointOfInterestHelper.register(key, builder.ticketCount(), builder.searchDistance(), builder.blocks());
        return new FabricRegistryReference<>(value, key, Registry.POINT_OF_INTEREST_TYPE);
    }

    /**
     * creates a new registry manager for <code>modId</code> or returns an existing one
     *
     * @param modId         namespace used for registration
     * @param deferred      defer registration for this manager until {@link #applyRegistration()} is called
     * @return              new mod specific registry manager
     */
    public synchronized static RegistryManager of(String modId, boolean deferred) {
        FabricRegistryManager registryManager = MOD_TO_REGISTRY.computeIfAbsent(modId, modId1 -> new FabricRegistryManager(modId1, deferred));
        if (deferred != registryManager.deferred) throw new IllegalArgumentException("deferred: %s does not match value set for existing RegistryManager".formatted(deferred));
        return registryManager;
    }
}
