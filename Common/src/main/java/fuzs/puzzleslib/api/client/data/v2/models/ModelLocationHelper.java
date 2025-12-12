package fuzs.puzzleslib.api.client.data.v2.models;

import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * A utility for managing model and texture locations for blocks and items.
 */
public final class ModelLocationHelper {
    /**
     * The block model / texture path prefix.
     */
    public static final String BLOCK_PATH = "block";
    /**
     * The item model / texture path prefix.
     */
    public static final String ITEM_PATH = "item";

    private ModelLocationHelper() {
        // NO-OP
    }

    /**
     * @param block the block
     * @return the default block model location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockModel(Block block) {
        return ModelLocationUtils.getModelLocation(block);
    }

    /**
     * @param block  the block
     * @param suffix the block model suffix
     * @return the block model location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockModel(Block block, String suffix) {
        return ModelLocationUtils.getModelLocation(block, suffix);
    }

    /**
     * @param identifier the block location
     * @return the default block model location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockModel(Identifier identifier) {
        return identifier.withPrefix(BLOCK_PATH + "/");
    }

    /**
     * @param identifier the block location
     * @param suffix           the block model suffix
     * @return the block model location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockModel(Identifier identifier, String suffix) {
        return getBlockModel(identifier).withSuffix(suffix);
    }

    /**
     * @param block the block
     * @return the default block texture location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockTexture(Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    /**
     * @param block  the block
     * @param suffix the block texture suffix
     * @return the block texture location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockTexture(Block block, String suffix) {
        return TextureMapping.getBlockTexture(block, suffix);
    }

    /**
     * @param identifier the block location
     * @return the default block texture location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockTexture(Identifier identifier) {
        return identifier.withPrefix(BLOCK_PATH + "/");
    }

    /**
     * @param identifier the block location
     * @param suffix           the block texture suffix
     * @return the block texture location, prefixed with {@link #BLOCK_PATH}
     */
    public static Identifier getBlockTexture(Identifier identifier, String suffix) {
        return getBlockTexture(identifier).withSuffix(suffix);
    }

    /**
     * @param block the block
     * @return the block registry key
     */
    public static Identifier getBlockLocation(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    /**
     * @param block  the block
     * @param suffix the block key suffix
     * @return the block registry key
     */
    public static Identifier getBlockLocation(Block block, String suffix) {
        return getBlockLocation(block).withSuffix(suffix);
    }

    /**
     * @param block the block
     * @return the block registry key name
     */
    public static String getBlockName(Block block) {
        return getBlockLocation(block).getPath();
    }

    /**
     * @param item the item
     * @return the default item model location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemModel(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    /**
     * @param item   the item
     * @param suffix the item model suffix
     * @return the item model location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemModel(Item item, String suffix) {
        return ModelLocationUtils.getModelLocation(item, suffix);
    }

    /**
     * @param identifier the item location
     * @return the default item model location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemModel(Identifier identifier) {
        return identifier.withPrefix(ITEM_PATH + "/");
    }

    /**
     * @param identifier the item location
     * @param suffix           the item model suffix
     * @return the item model location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemModel(Identifier identifier, String suffix) {
        return getItemModel(identifier).withSuffix(suffix);
    }

    /**
     * @param item the item
     * @return the default item texture location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemTexture(Item item) {
        return TextureMapping.getItemTexture(item);
    }

    /**
     * @param item   the item
     * @param suffix the item texture suffix
     * @return the item texture location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemTexture(Item item, String suffix) {
        return TextureMapping.getItemTexture(item, suffix);
    }

    /**
     * @param identifier the item location
     * @return the default item texture location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemTexture(Identifier identifier) {
        return identifier.withPrefix(ITEM_PATH + "/");
    }

    /**
     * @param identifier the item location
     * @param suffix           the item texture suffix
     * @return the item texture location, prefixed with {@link #ITEM_PATH}
     */
    public static Identifier getItemTexture(Identifier identifier, String suffix) {
        return getItemTexture(identifier).withSuffix(suffix);
    }

    /**
     * @param item the item
     * @return the item registry key
     */
    public static Identifier getItemLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    /**
     * @param item   the item
     * @param suffix the item key suffix
     * @return the item registry key
     */
    public static Identifier getItemLocation(Item item, String suffix) {
        return getItemLocation(item).withSuffix(suffix);
    }

    /**
     * @param item the item
     * @return the item registry key name
     */
    public static String getItemName(Item item) {
        return getItemLocation(item).getPath();
    }
}
