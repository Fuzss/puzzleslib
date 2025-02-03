package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Create new {@link Registry Registries}.
 */
public interface RegistryFactory {
    /**
     * the instance
     */
    RegistryFactory INSTANCE = CommonFactories.INSTANCE.getRegistryFactory();

    /**
     * Create an un-synchronized {@link net.minecraft.core.DefaultedMappedRegistry}.
     *
     * @param registryKey the registry key
     * @param <T>         the registry value type
     * @return the new registry
     */
    default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey) {
        return this.create(registryKey, (String) null);
    }

    /**
     * Create a synchronized {@link net.minecraft.core.DefaultedMappedRegistry}, so that numeric registry ids can be
     * used in networking.
     *
     * @param registryKey the registry key
     * @param <T>         the registry value type
     * @return the new registry
     */
    default <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey) {
        return this.createSynced(registryKey, (String) null);
    }

    /**
     * Create an un-synchronized {@link net.minecraft.core.DefaultedMappedRegistry}.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param <T>         the registry value type
     * @return the new registry
     */
    default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, @Nullable String defaultKey) {
        return this.create(registryKey, defaultKey != null ? registryKey.location().withPath(defaultKey) : null);
    }

    /**
     * Create a synchronized {@link net.minecraft.core.DefaultedMappedRegistry}, so that numeric registry ids can be
     * used in networking.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param <T>         the registry value type
     * @return the new registry
     */
    default <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey, @Nullable String defaultKey) {
        return this.createSynced(registryKey, defaultKey != null ? registryKey.location().withPath(defaultKey) : null);
    }

    /**
     * Create an un-synchronized {@link net.minecraft.core.DefaultedMappedRegistry}.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param <T>         the registry value type
     * @return the new registry
     */
    <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, @Nullable ResourceLocation defaultKey);

    /**
     * Create a synchronized {@link net.minecraft.core.DefaultedMappedRegistry}, so that numeric registry ids can be
     * used in networking.
     *
     * @param registryKey the registry key
     * @param defaultKey  the default value key
     * @param <T>         the registry value type
     * @return the new registry
     */
    <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey, @Nullable ResourceLocation defaultKey);
}
