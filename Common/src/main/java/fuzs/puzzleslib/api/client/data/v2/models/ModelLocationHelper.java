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

    public static ResourceLocation getBlockModel(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(BLOCK_PATH + "/");
    }

    public static ResourceLocation getBlockTexture(Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    public static ResourceLocation getBlockTexture(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(BLOCK_PATH + "/");
    }

    public static ResourceLocation getBlockLocation(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static String getBlockName(Block block) {
        return getBlockLocation(block).getPath();
    }

    public static ResourceLocation getItemModel(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    public static ResourceLocation getItemModel(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(ITEM_PATH + "/");
    }

    public static ResourceLocation getItemTexture(Item item) {
        return TextureMapping.getItemTexture(item);
    }

    public static ResourceLocation getItemTexture(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(ITEM_PATH + "/");
    }

    public static ResourceLocation getItemLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getItemName(Item item) {
        return getItemLocation(item).getPath();
    }
}
