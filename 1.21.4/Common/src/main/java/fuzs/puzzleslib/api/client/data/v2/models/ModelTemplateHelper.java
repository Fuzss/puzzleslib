package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;
import java.util.function.BiConsumer;

public final class ModelTemplateHelper {
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

    public static ResourceLocation generateFlatItem(Item item, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelTemplate, modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, Item layerItem, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelTemplate,
                modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(resourceLocation, resourceLocation, modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(resourceLocation, resourceLocation, modelTemplate, modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateFlatItem(resourceLocation, layer0, ModelTemplates.FLAT_ITEM, modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return modelTemplate.create(ModelLocationHelper.getItemModel(resourceLocation),
                TextureMapping.layer0(ModelLocationHelper.getItemTexture(layer0)),
                modelOutput);
    }

    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item), layer0, layer1, modelOutput);
    }

    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item),
                layer0,
                layer1,
                modelTemplate,
                modelOutput);
    }

    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return generateLayeredItem(resourceLocation, layer0, layer1, ModelTemplates.TWO_LAYERED_ITEM, modelOutput);
    }

    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return modelTemplate.create(ModelLocationHelper.getItemModel(resourceLocation),
                TextureMapping.layered(ModelLocationHelper.getItemTexture(layer0),
                        ModelLocationHelper.getItemTexture(layer1)),
                modelOutput);
    }
}
