package fuzs.puzzleslib.api.network.v3.serialization;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * A few additional more or less useful stream codecs.
 */
public final class ExtraStreamCodecs {
    /**
     * {@link ResourceKey} stream codec
     */
    public static final StreamCodec<ByteBuf, ResourceKey<?>> DIRECT_RESOURCE_KEY = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ResourceKey::registry,
            ResourceLocation.STREAM_CODEC,
            ResourceKey::location,
            (ResourceLocation registry, ResourceLocation location) -> {
                return ResourceKey.create(ResourceKey.createRegistryKey(registry), location);
            }
    );

    private ExtraStreamCodecs() {
        // NO-OP
    }
}
