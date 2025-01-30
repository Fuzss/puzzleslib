package fuzs.puzzleslib.api.core.v1.context;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Register data pack-driven dynamic registries.
 * <p>
 * TODO merge normal registry registration into this
 */
public interface DataPackRegistriesContext {

    /**
     * Registers an un-synchronized dynamic registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serializing registry elements
     * @param <T>         the registry element type
     */
    <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec);

    /**
     * Registers a synchronized dynamic registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serializing registry elements
     * @param <T>         the registry element type
     */
    default <T> void registerSynced(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        this.registerSynced(registryKey, codec, codec);
    }

    /**
     * Registers a synchronized dynamic registry.
     *
     * @param registryKey  the registry resource key
     * @param codec        the codec for serializing registry elements
     * @param networkCodec an optional more compressed network codec for serializing registry elements for
     *                     synchronization across networks
     * @param <T>          the registry element type
     */
    <T> void registerSynced(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec);
}
