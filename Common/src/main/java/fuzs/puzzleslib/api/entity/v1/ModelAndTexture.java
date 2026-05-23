package fuzs.puzzleslib.api.entity.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.core.v2.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Copied from Minecraft 26.1.
 */
public record ModelAndTexture<T>(T model, ClientAsset.ResourceTexture asset) {
    public ModelAndTexture(T model, ResourceLocation assetId) {
        this(model, new ClientAsset.ResourceTexture(assetId));
    }

    public static <T> MapCodec<ModelAndTexture<T>> codec(Codec<T> modelCodec, T defaultModel) {
        return RecordCodecBuilder.mapCodec(i -> i.group(modelCodec.optionalFieldOf("model", defaultModel)
                                .forGetter(ModelAndTexture::model),
                        ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ModelAndTexture::asset))
                .apply(i, ModelAndTexture::new));
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, ModelAndTexture<T>> streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> modelCodec) {
        return StreamCodec.composite(modelCodec,
                ModelAndTexture::model,
                ClientAsset.ResourceTexture.STREAM_CODEC,
                ModelAndTexture::asset,
                ModelAndTexture::new);
    }
}
