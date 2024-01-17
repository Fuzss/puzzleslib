package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import net.minecraft.world.level.ItemLike;

/**
 * Register a custom inventory renderer for an item belonging to a block entity.
 */
public interface BuiltinModelItemRendererContext {

    /**
     * Register a {@link BuiltinItemRenderer} for one or more items.
     *
     * @param renderer dynamic implementation of {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
     * @param items    items to register for
     */
    void registerItemRenderer(BuiltinItemRenderer renderer, ItemLike... items);

    /**
     * Register a {@link ReloadingBuiltInItemRenderer} for one or more items.
     *
     * @param renderer dynamic implementation of {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
     * @param items    items to register for
     */
    void registerItemRenderer(ReloadingBuiltInItemRenderer renderer, ItemLike... items);
}
