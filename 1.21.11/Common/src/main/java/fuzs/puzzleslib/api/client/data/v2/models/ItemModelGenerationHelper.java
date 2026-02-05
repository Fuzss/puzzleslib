package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ItemModelGenerationHelper {
    public static final ModelTemplate HORN = ModelTemplateHelper.createItemModelTemplate(Identifier.withDefaultNamespace(
            "goat_horn"), TextureSlot.LAYER0);
    public static final ModelTemplate TOOTING_HORN = ModelTemplateHelper.createItemModelTemplate(Identifier.withDefaultNamespace(
            "tooting_goat_horn"), TextureSlot.LAYER0);
    public static final ModelTemplate SHIELD_MODEL_TEMPLATE = ModelTemplateHelper.createItemModelTemplate(Identifier.withDefaultNamespace(
            "shield"), TextureSlot.PARTICLE);
    public static final ModelTemplate SHIELD_BLOCKING_MODEL_TEMPLATE = ModelTemplateHelper.createItemModelTemplate(
            Identifier.withDefaultNamespace("shield_blocking"),
            TextureSlot.PARTICLE);

    private ItemModelGenerationHelper() {
        // NO-OP
    }

    public static void generateFlatItem(Item item, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item, modelTemplate, itemModelGenerators.modelOutput)));
    }

    public static Identifier createFlatItemModel(Item item, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(item, item, modelTemplate, modelOutput);
    }

    public static void generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item,
                        layerItem,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    public static Identifier createFlatItemModel(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(item, ModelLocationHelper.getItemModel(layerItem), modelTemplate, modelOutput);
    }

    public static void generateFlatItem(Item item, Identifier layer0Location, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item,
                        layer0Location,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    public static Identifier createFlatItemModel(Item item, Identifier layer0Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(ModelLocationHelper.getItemModel(item), layer0Location, modelTemplate, modelOutput);
    }

    public static Identifier createFlatItemModel(Identifier identifier, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(identifier, identifier, modelTemplate, modelOutput);
    }

    public static Identifier createFlatItemModel(Identifier identifier, Identifier layer0Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return modelTemplate.create(identifier, TextureMapping.layer0(layer0Location), modelOutput);
    }

    public static void generateLayeredItem(Item item, Identifier layer0Location, Identifier layer1Location, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createLayeredItemModel(item,
                        layer0Location,
                        layer1Location,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    public static Identifier createLayeredItemModel(Item item, Identifier layer0Location, Identifier layer1Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createLayeredItemModel(ModelLocationHelper.getItemModel(item),
                layer0Location,
                layer1Location,
                modelTemplate,
                modelOutput);
    }

    public static Identifier createLayeredItemModel(Identifier identifier, Identifier layer0Location, Identifier layer1Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return modelTemplate.create(identifier, TextureMapping.layered(layer0Location, layer1Location), modelOutput);
    }

    /**
     * @see ItemModelGenerators#generateBow(Item)
     */
    public static void generateBow(Item item, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.createFlatItemModel(item, ModelTemplates.BOW);
        itemModelGenerators.generateBow(item);
    }

    /**
     * @see ItemModelGenerators#generateGoatHorn(Item)
     */
    public static void generateHorn(Item item, ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked baseModel = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, HORN));
        ItemModel.Unbaked tootingModel = ItemModelUtils.plainModel(createFlatItemModel(ModelLocationHelper.getItemModel(
                item,
                "_tooting"), ModelLocationHelper.getItemModel(item), TOOTING_HORN, itemModelGenerators.modelOutput));
        itemModelGenerators.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), tootingModel, baseModel);
    }

    /**
     * @see BlockModelGenerators#createHead(Block, Block, SkullBlock.Type, Identifier)
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
        Identifier baseModel = SHIELD_MODEL_TEMPLATE.create(ModelLocationHelper.getItemModel(item),
                TextureMapping.particle(particleBlock),
                itemModelGenerators.modelOutput);
        Identifier blockingModel = SHIELD_BLOCKING_MODEL_TEMPLATE.create(ModelLocationHelper.getItemModel(item,
                "_blocking"), TextureMapping.particle(particleBlock), itemModelGenerators.modelOutput);
        ItemModel.Unbaked unbaked = ItemModelUtils.specialModel(baseModel, specialModelSupplier.get());
        ItemModel.Unbaked unbaked2 = ItemModelUtils.specialModel(blockingModel, specialModelSupplier.get());
        itemModelGenerators.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), unbaked2, unbaked);
    }

    /**
     * @see BlockModelGenerators#createChest(Block, Block, Identifier, boolean)
     */
    public static void generateChest(Block chestBlock, Block particleBlock, Identifier itemTexture, boolean useGiftTexture, Function<Identifier, SpecialModelRenderer.Unbaked> unbakedRendererFactory, BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(chestBlock, particleBlock);
        Item item = chestBlock.asItem();
        Identifier identifier = ModelTemplates.CHEST_INVENTORY.create(item,
                TextureMapping.particle(particleBlock),
                blockModelGenerators.modelOutput);
        ItemModel.Unbaked itemModel = ItemModelUtils.specialModel(identifier,
                unbakedRendererFactory.apply(itemTexture));
        if (useGiftTexture) {
            ItemModel.Unbaked unbaked2 = ItemModelUtils.specialModel(identifier,
                    unbakedRendererFactory.apply(ChestSpecialRenderer.GIFT_CHEST_TEXTURE));
            blockModelGenerators.itemModelOutput.accept(item, ItemModelUtils.isXmas(unbaked2, itemModel));
        } else {
            blockModelGenerators.itemModelOutput.accept(item, itemModel);
        }
    }
}
