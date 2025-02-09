package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModelLocationHelper {
    public static final String BLOCK_PATH = "block";
    public static final String ITEM_PATH = "item";

    private ModelLocationHelper() {
        // NO-OP
    }

    public static ResourceLocation getBlockModel(Block block) {
        return ModelLocationUtils.getModelLocation(block);
    }

    public static ResourceLocation getBlockModel(Block block, String suffix) {
        return ModelLocationUtils.getModelLocation(block, suffix);
    }

    public static ResourceLocation getBlockModel(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(BLOCK_PATH + "/");
    }

    public static ResourceLocation getBlockModel(ResourceLocation resourceLocation, String suffix) {
        return getBlockModel(resourceLocation).withSuffix(suffix);
    }

    public static ResourceLocation getBlockTexture(Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    public static ResourceLocation getBlockTexture(Block block, String suffix) {
        return TextureMapping.getBlockTexture(block, suffix);
    }

    public static ResourceLocation getBlockTexture(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(BLOCK_PATH + "/");
    }

    public static ResourceLocation getBlockTexture(ResourceLocation resourceLocation, String suffix) {
        return getBlockTexture(resourceLocation).withSuffix(suffix);
    }

    public static ResourceLocation getBlockLocation(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation getBlockLocation(Block block, String suffix) {
        return getBlockLocation(block).withSuffix(suffix);
    }

    public static String getBlockName(Block block) {
        return getBlockLocation(block).getPath();
    }

    public static ResourceLocation getItemModel(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    public static ResourceLocation getItemModel(Item item, String suffix) {
        return ModelLocationUtils.getModelLocation(item, suffix);
    }

    public static ResourceLocation getItemModel(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(ITEM_PATH + "/");
    }

    public static ResourceLocation getItemModel(ResourceLocation resourceLocation, String suffix) {
        return getItemModel(resourceLocation).withSuffix(suffix);
    }

    public static ResourceLocation getItemTexture(Item item) {
        return TextureMapping.getItemTexture(item);
    }

    public static ResourceLocation getItemTexture(Item item, String suffix) {
        return TextureMapping.getItemTexture(item, suffix);
    }

    public static ResourceLocation getItemTexture(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(ITEM_PATH + "/");
    }

    public static ResourceLocation getItemTexture(ResourceLocation resourceLocation, String suffix) {
        return getItemTexture(resourceLocation).withSuffix(suffix);
    }

    public static ResourceLocation getItemLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation getItemLocation(Item item, String suffix) {
        return getItemLocation(item).withSuffix(suffix);
    }

    public static String getItemName(Item item) {
        return getItemLocation(item).getPath();
    }
}
