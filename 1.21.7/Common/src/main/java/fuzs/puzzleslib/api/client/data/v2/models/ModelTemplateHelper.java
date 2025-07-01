package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public final class ModelTemplateHelper {

    private ModelTemplateHelper() {
        // NO-OP
    }

    public static ModelTemplate createBlockModelTemplate(ResourceLocation resourceLocation, TextureSlot... requiredSlots) {
        return createBlockModelTemplate(resourceLocation, "", requiredSlots);
    }

    public static ModelTemplate createBlockModelTemplate(ResourceLocation resourceLocation, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(ModelLocationHelper.getBlockModel(resourceLocation)),
                Optional.of(suffix),
                requiredSlots);
    }

    public static ModelTemplate createItemModelTemplate(ResourceLocation resourceLocation, TextureSlot... requiredSlots) {
        return createItemModelTemplate(resourceLocation, "", requiredSlots);
    }

    public static ModelTemplate createItemModelTemplate(ResourceLocation resourceLocation, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(ModelLocationHelper.getItemModel(resourceLocation)),
                Optional.of(suffix),
                requiredSlots);
    }

    public static TextureMapping createParticleTextureMapping(Block block) {
        return createParticleTextureMapping(block, "");
    }

    public static TextureMapping createParticleTextureMapping(Block block, String suffix) {
        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(block, suffix);
        return new TextureMapping().put(TextureSlot.TEXTURE, resourceLocation)
                .put(TextureSlot.PARTICLE, resourceLocation);
    }

    public static TextureMapping createSingleSlotMapping(TextureSlot textureSlot, Block block) {
        return TextureMapping.singleSlot(textureSlot, TextureMapping.getBlockTexture(block));
    }
}
