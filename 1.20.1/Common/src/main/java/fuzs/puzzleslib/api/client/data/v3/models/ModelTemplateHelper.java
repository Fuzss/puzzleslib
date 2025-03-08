package fuzs.puzzleslib.api.client.data.v3.models;

import com.google.gson.JsonElement;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    public static ResourceLocation generateFlatItem(Item item, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelTemplate, modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item), modelTemplate, modelOutput, factory);
    }

    public static ResourceLocation generateFlatItem(Item item, Item layerItem, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelTemplate,
                modelOutput);
    }

    public static ResourceLocation generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return generateFlatItem(ModelLocationHelper.getItemLocation(item),
                ModelLocationHelper.getItemLocation(layerItem),
                modelTemplate,
                modelOutput,
                factory);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(resourceLocation, resourceLocation, modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(resourceLocation, resourceLocation, modelTemplate, modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return generateFlatItem(resourceLocation, resourceLocation, modelTemplate, modelOutput, factory);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(resourceLocation, layer0, ModelTemplates.FLAT_ITEM, modelOutput);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateFlatItem(resourceLocation,
                layer0,
                modelTemplate,
                modelOutput,
                modelTemplate::createBaseTemplate);
    }

    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ResourceLocation layer0, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return modelTemplate.create(ModelLocationHelper.getItemModel(resourceLocation),
                TextureMapping.layer0(ModelLocationHelper.getItemTexture(layer0)),
                modelOutput,
                factory);
    }

    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item), layer0, layer1, modelOutput);
    }

    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item),
                layer0,
                layer1,
                modelTemplate,
                modelOutput);
    }

    public static ResourceLocation generateLayeredItem(Item item, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return generateLayeredItem(ModelLocationHelper.getItemLocation(item),
                layer0,
                layer1,
                modelTemplate,
                modelOutput,
                factory);
    }

    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateLayeredItem(resourceLocation, layer0, layer1, ModelTemplates.TWO_LAYERED_ITEM, modelOutput);
    }

    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return generateLayeredItem(resourceLocation,
                layer0,
                layer1,
                modelTemplate,
                modelOutput,
                modelTemplate::createBaseTemplate);
    }

    public static ResourceLocation generateLayeredItem(ResourceLocation resourceLocation, ResourceLocation layer0, ResourceLocation layer1, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return modelTemplate.create(ModelLocationHelper.getItemModel(resourceLocation),
                TextureMapping.layered(ModelLocationHelper.getItemTexture(layer0),
                        ModelLocationHelper.getItemTexture(layer1)),
                modelOutput,
                factory);
    }
}
