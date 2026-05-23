package fuzs.puzzleslib.api.core.v2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Copied from Minecraft 26.1.
 */
public interface ClientAsset {
    ResourceLocation id();

    record DownloadedTexture(ResourceLocation texturePath, String url) implements Texture {
        @Override
        public ResourceLocation id() {
            return this.texturePath;
        }
    }

    record ResourceTexture(ResourceLocation id, ResourceLocation texturePath) implements Texture {
        public static final Codec<ResourceTexture> CODEC = ResourceLocation.CODEC.xmap(ResourceTexture::new,
                ResourceTexture::id);
        public static final MapCodec<ResourceTexture> DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
        public static final StreamCodec<ByteBuf, ResourceTexture> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(
                ResourceTexture::new,
                ResourceTexture::id);

        public ResourceTexture(ResourceLocation texture) {
            this(texture, texture.withPath(path -> "textures/" + path + ".png"));
        }
    }

    interface Texture extends ClientAsset {
        ResourceLocation texturePath();
    }
}
