package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Create and register new {@link Registry Registries}.
 * <p>
 * TODO rework this to use a ModConstructor event, also make creating synced registries separate methods instead of parameter, so {@code createSynced} not just {@code create} without the parameter
 */
public interface RegistryFactory {
    /**
     * the instance
     */
    RegistryFactory INSTANCE = CommonFactories.INSTANCE.getRegistryFactory();

    /**
     * Create and register a {@link net.minecraft.core.MappedRegistry}.
     * <p>
     * Calls {@link #register(Registry)} as part of the implementation.
     * <p>
     * Registry contents are synced to clients.
     *
     * @param registryKey the registry key
     * @param <T>         registry values type
     * @return the new registry
     */
    default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey) {
        return this.create(registryKey, true);
    }

    /**
     * Create and register a {@link net.minecraft.core.MappedRegistry}.
     * <p>
     * Calls {@link #register(Registry)} as part of the implementation.
     *
     * @param registryKey the registry key
     * @param synced      sync registry contents to clients, so that numeric registry ids can be used in networking
     * @param <T>         registry values type
     * @return the new registry
     */
    <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, boolean synced);

    /**
     * Create and register a {@link net.minecraft.core.DefaultedMappedRegistry}.
     * <p>
     * Calls {@link #register(Registry)} as part of the implementation.
     * <p>
     * Registry contents are synced to clients.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param <T>         registry values type
     * @return the new registry
     */
    default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, String defaultKey) {
        return this.create(registryKey, defaultKey, true);
    }

    /**
     * Create and register a {@link net.minecraft.core.DefaultedMappedRegistry}.
     * <p>
     * Calls {@link #register(Registry)} as part of the implementation.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param synced      sync registry contents to clients, so that numeric registry ids can be used in networking
     * @param <T>         registry values type
     * @return the new registry
     */
    default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, String defaultKey, boolean synced) {
        return this.create(registryKey, registryKey.location().withPath(defaultKey), synced);
    }

    /**
     * Create and register a {@link net.minecraft.core.DefaultedMappedRegistry}.
     * <p>
     * Calls {@link #register(Registry)} as part of the implementation.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param synced      sync registry contents to clients, so that numeric registry ids can be used in networking
     * @param <T>         registry values type
     * @return the new registry
     */
    <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, ResourceLocation defaultKey, boolean synced);

    /**
     * Register an already constructed {@link Registry}.
     *
     * @param registry the registry
     * @param <T>      registry values type
     * @return the registry
     */
    <T> Registry<T> register(Registry<T> registry);
}
