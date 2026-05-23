package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.client.renderer.v1.special.SpecialModelRenderer;
import fuzs.puzzleslib.impl.client.renderer.SpecialBuiltInItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

/**
 * Register a custom renderer for an item.
 */
public interface BuiltinModelItemRendererContext {

    /**
     * Register a {@link BuiltinItemRenderer} for an item.
     *
     * @param item     the item
     * @param renderer the custom implementation of
     *                 {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
     */
    void registerItemRenderer(Item item, BuiltinItemRenderer renderer);

    @Deprecated
    default void registerItemRenderer(BuiltinItemRenderer renderer, ItemLike... items) {
        Objects.requireNonNull(items, "items is null");
        for (ItemLike item : items) {
            this.registerItemRenderer(item.asItem(), renderer);
        }
    }

    /**
     * Register a {@link ReloadingBuiltInItemRenderer} for an item.
     *
     * @param item     the item
     * @param renderer the custom implementation of
     *                 {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
     */
    void registerItemRenderer(Item item, ReloadingBuiltInItemRenderer renderer);

    @Deprecated
    default void registerItemRenderer(ReloadingBuiltInItemRenderer renderer, ItemLike... items) {
        Objects.requireNonNull(items, "items is null");
        for (ItemLike item : items) {
            this.registerItemRenderer(item.asItem(), renderer);
        }
    }

    /**
     * Register a {@link SpecialModelRenderer.Unbaked} for an item.
     *
     * @param item     the item
     * @param renderer the special model renderer
     */
    default void registerItemRenderer(Item item, SpecialModelRenderer.Unbaked<?> specialModelRenderer) {
        this.registerItemRenderer(item, new SpecialBuiltInItemRenderer<>(specialModelRenderer));
    }
}
