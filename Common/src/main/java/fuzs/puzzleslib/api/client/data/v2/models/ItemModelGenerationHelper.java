package fuzs.puzzleslib.api.client.data.v2.models;

import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.item.LegacySpawnEggItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ItemModelGenerationHelper {
    public static final ModelTemplate HORN = ModelTemplateHelper.createItemModelTemplate(ResourceLocation.withDefaultNamespace(
            "goat_horn"), TextureSlot.LAYER0);
    public static final ModelTemplate TOOTING_HORN = ModelTemplateHelper.createItemModelTemplate(ResourceLocation.withDefaultNamespace(
            "tooting_goat_horn"), TextureSlot.LAYER0);
    public static final ModelTemplate SHIELD_MODEL_TEMPLATE = ModelTemplateHelper.createItemModelTemplate(
            ResourceLocation.withDefaultNamespace("shield"),
            TextureSlot.PARTICLE);
    public static final ModelTemplate SHIELD_BLOCKING_MODEL_TEMPLATE = ModelTemplateHelper.createItemModelTemplate(
            ResourceLocation.withDefaultNamespace("shield_blocking"),
            TextureSlot.PARTICLE);

    private ItemModelGenerationHelper() {
        // NO-OP
    }

    public static void generateFlatItem(Item item, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item, modelTemplate, itemModelGenerators.modelOutput)));
    }

    public static ResourceLocation createFlatItemModel(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return createFlatItemModel(item, item, modelTemplate, modelOutput);
    }

    public static void generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item,
                        layerItem,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    public static ResourceLocation createFlatItemModel(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return createFlatItemModel(item, ModelLocationHelper.getItemModel(layerItem), modelTemplate, modelOutput);
    }

    public static void generateFlatItem(Item item, ResourceLocation layer0Location, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item,
                        layer0Location,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    public static ResourceLocation createFlatItemModel(Item item, ResourceLocation layer0Location, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return createFlatItemModel(ModelLocationHelper.getItemModel(item), layer0Location, modelTemplate, modelOutput);
    }

    public static ResourceLocation createFlatItemModel(ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return createFlatItemModel(resourceLocation, resourceLocation, modelTemplate, modelOutput);
    }

    public static ResourceLocation createFlatItemModel(ResourceLocation resourceLocation, ResourceLocation layer0Location, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return modelTemplate.create(resourceLocation, TextureMapping.layer0(layer0Location), modelOutput);
    }

    public static void generateLayeredItem(Item item, ResourceLocation layer0Location, ResourceLocation layer1Location, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createLayeredItemModel(item,
                        layer0Location,
                        layer1Location,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    public static ResourceLocation createLayeredItemModel(Item item, ResourceLocation layer0Location, ResourceLocation layer1Location, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return createLayeredItemModel(ModelLocationHelper.getItemModel(item),
                layer0Location,
                layer1Location,
                modelTemplate,
                modelOutput);
    }

    public static ResourceLocation createLayeredItemModel(ResourceLocation resourceLocation, ResourceLocation layer0Location, ResourceLocation layer1Location, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        return modelTemplate.create(resourceLocation,
                TextureMapping.layered(layer0Location, layer1Location),
                modelOutput);
    }

    /**
     * @see ItemModelGenerators#generateBow(Item)
     */
    public static void generateBow(Item item, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.createFlatItemModel(item, ModelTemplates.BOW);
        itemModelGenerators.generateBow(item);
    }

    @Deprecated(forRemoval = true)
    public static void generateSpawnEgg(Item item, ItemModelGenerators itemModelGenerators) {
        generateSpawnEgg(item,
                ((LegacySpawnEggItem) item).getBackgroundColor(),
                ((LegacySpawnEggItem) item).getHighlightColor(),
                itemModelGenerators);
    }

    @Deprecated(forRemoval = true)
    public static void generateSpawnEgg(Item item, int primaryColor, int secondaryColor, ItemModelGenerators itemModelGenerators) {
        ResourceLocation textureLocation = PuzzlesLibMod.id("spawn_egg");
        ResourceLocation resourceLocation = itemModelGenerators.generateLayeredItem(item,
                ModelLocationHelper.getItemTexture(textureLocation),
                ModelLocationHelper.getItemTexture(textureLocation, "_overlay"));
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.tintedModel(resourceLocation,
                        ItemModelUtils.constantTint(primaryColor),
                        ItemModelUtils.constantTint(secondaryColor)));
    }

    /**
     * @see ItemModelGenerators#generateGoatHorn(Item)
     */
    public static void generateHorn(Item item, ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked falseModel = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, HORN));
        ItemModel.Unbaked trueModel = ItemModelUtils.plainModel(createFlatItemModel(ModelLocationHelper.getItemModel(
                item,
                "_tooting"), ModelLocationHelper.getItemModel(item), TOOTING_HORN, itemModelGenerators.modelOutput));
        itemModelGenerators.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), trueModel, falseModel);
    }

    /**
     * @see BlockModelGenerators#createHead(Block, Block, SkullBlock.Type, ResourceLocation)
     */
    public static void generateHead(Block headBlock, Block wallHeadBlock, SkullBlock.Type type, BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createHead(headBlock,
                wallHeadBlock,
                type,
                ModelLocationUtils.decorateItemModelLocation("template_skull"));
    }

    /**
     * @see ItemModelGenerators#generateShield(Item)
     */
    public static void generateShield(Item item, Block particleBlock, Supplier<SpecialModelRenderer.Unbaked> specialModelSupplier, ItemModelGenerators itemModelGenerators) {
        ResourceLocation resourceLocation = SHIELD_MODEL_TEMPLATE.create(ModelLocationHelper.getItemModel(item),
                TextureMapping.particle(particleBlock),
                itemModelGenerators.modelOutput);
        ResourceLocation resourceLocation2 = SHIELD_BLOCKING_MODEL_TEMPLATE.create(ModelLocationHelper.getItemModel(item,
                "_blocking"), TextureMapping.particle(particleBlock), itemModelGenerators.modelOutput);
        ItemModel.Unbaked unbaked = ItemModelUtils.specialModel(resourceLocation, specialModelSupplier.get());
        ItemModel.Unbaked unbaked2 = ItemModelUtils.specialModel(resourceLocation2, specialModelSupplier.get());
        itemModelGenerators.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), unbaked2, unbaked);
    }
}
