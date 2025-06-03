package fuzs.puzzleslib.impl.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceKey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry for {@link StreamCodec} implementations.
 */
@Deprecated
public interface StreamCodecRegistry<T extends StreamCodecRegistry<T>> {

    /**
     * Register a stream codec by providing an encoder and a decoder.
     *
     * @param type    the data type
     * @param encoder write to byte buffer
     * @param decoder read from byte buffer
     * @param <V>     data type
     */
    default <B extends ByteBuf, V> T registerSerializer(Class<V> type, StreamEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return this.registerSerializer(type, StreamCodec.of(encoder, decoder));
    }

    /**
     * Register a stream codec for a vanilla registry.
     *
     * @param type        the data type
     * @param resourceKey registry resource key
     * @param <V>         data type
     */
    @SuppressWarnings("unchecked")
    default <V> T registerSerializer(Class<? super V> type, ResourceKey<Registry<V>> resourceKey) {
        return this.registerSerializer((Class<V>) type, ByteBufCodecs.registry(resourceKey));
    }

    /**
     * Register a stream codec.
     *
     * @param type        the data type
     * @param streamCodec the stream codec
     * @param <B>         byte buffer type
     * @param <V>         data type
     */
    <B extends ByteBuf, V> T registerSerializer(Class<V> type, StreamCodec<? super B, V> streamCodec);

    /**
     * Register a custom serializer for container types. Subclasses are supported, meaning e.g. any map implementation
     * will be handled by a provider registered for {@link Map}.
     * <p>
     * All types extending collection are by default deserialized in a {@link LinkedHashSet}. To enable a specific
     * collection type, a unique serializer must be registered. This is already done for {@link List Lists}, which are
     * deserialized as {@link ArrayList}.
     *
     * @param type    container type
     * @param factory new empty collection provider (preferable with pre-configured size)
     * @param <V>     container type
     */
    <B extends ByteBuf, V> T registerContainerProvider(Class<V> type, Function<Type[], StreamCodec<? super B, ? extends V>> factory);
}
