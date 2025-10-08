package fuzs.puzzleslib.api.core.v1.context;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Register data pack-driven dynamic registries.
 */
public interface DataPackRegistriesContext {

    /**
     * Registers an unsynchronised dynamic registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serialising registry values
     * @param <T>         the registry value type
     */
    <T> void registerRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec);

    /**
     * Registers a synchronised dynamic registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serialising registry values
     * @param <T>         the registry value type
     */
    default <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        this.registerSyncedRegistry(registryKey, codec, codec);
    }

    /**
     * Registers a synchronised dynamic registry.
     *
     * @param registryKey  the registry resource key
     * @param codec        the codec for serialising registry values
     * @param networkCodec an optional more compressed network codec for serialising registry values for synchronisation
     *                     across networks
     * @param <T>          the registry value type
     */
    <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec);
}
