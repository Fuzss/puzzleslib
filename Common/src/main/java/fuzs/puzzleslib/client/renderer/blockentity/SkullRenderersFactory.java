package fuzs.puzzleslib.client.renderer.blockentity;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.BiConsumer;

/**
 * Provides a way for adding custom skull type models to the immutable skull type map when it is created.
 */
@FunctionalInterface
public interface SkullRenderersFactory {

    /**
     * Adds the baked model to the context.
     *
     * @param entityModelSet model set for retrieving baked models
     * @param context        add model to context
     */
    void createSkullRenderers(EntityModelSet entityModelSet, BiConsumer<SkullBlock.Type, SkullModelBase> context);
}
