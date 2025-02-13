package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.BiConsumer;

public final class ModelTemplateHelper {
    @Deprecated(forRemoval = true)
    public static final ModelTemplate SPAWN_EGG = ModelTemplates.createItem("template_spawn_egg");

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

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(Item item, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelTemplate, modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(Item item, Item layerItem, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelTemplate,
                modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(resourceLocation, resourceLocation, modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(resourceLocation, resourceLocation, modelTemplate, modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(resourceLocation, layer0, ModelTemplates.FLAT_ITEM, modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return modelTemplate.create(ModelLocationHelper.getItemModel(resourceLocation),
                TextureMapping.layer0(ModelLocationHelper.getItemTexture(layer0)),
                modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item), layer0, layer1, modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item),
                layer0,
                layer1,
                modelTemplate,
                modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateLayeredItem(resourceLocation, layer0, layer1, ModelTemplates.TWO_LAYERED_ITEM, modelOutput);
    }

    @Deprecated(forRemoval = true)
    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return modelTemplate.create(ModelLocationHelper.getItemModel(resourceLocation),
                TextureMapping.layered(ModelLocationHelper.getItemTexture(layer0),
                        ModelLocationHelper.getItemTexture(layer1)),
                modelOutput);
    }
}
