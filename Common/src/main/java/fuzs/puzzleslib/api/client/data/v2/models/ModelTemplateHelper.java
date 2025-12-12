package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public final class ModelTemplateHelper {

    private ModelTemplateHelper() {
        // NO-OP
    }

    public static ModelTemplate createBlockModelTemplate(Identifier identifier, TextureSlot... requiredSlots) {
        return createBlockModelTemplate(identifier, "", requiredSlots);
    }

    public static ModelTemplate createBlockModelTemplate(Identifier identifier, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(ModelLocationHelper.getBlockModel(identifier)),
                Optional.of(suffix),
                requiredSlots);
    }

    public static ModelTemplate createItemModelTemplate(Identifier identifier, TextureSlot... requiredSlots) {
        return createItemModelTemplate(identifier, "", requiredSlots);
    }

    public static ModelTemplate createItemModelTemplate(Identifier identifier, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(ModelLocationHelper.getItemModel(identifier)),
                Optional.of(suffix),
                requiredSlots);
    }

    public static TextureMapping createParticleTextureMapping(Block block) {
        return createParticleTextureMapping(block, "");
    }

    public static TextureMapping createParticleTextureMapping(Block block, String suffix) {
        Identifier identifier = TextureMapping.getBlockTexture(block, suffix);
        return new TextureMapping().put(TextureSlot.TEXTURE, identifier)
                .put(TextureSlot.PARTICLE, identifier);
    }

    public static TextureMapping createSingleSlotMapping(TextureSlot textureSlot, Block block) {
        return TextureMapping.singleSlot(textureSlot, TextureMapping.getBlockTexture(block));
    }
}
