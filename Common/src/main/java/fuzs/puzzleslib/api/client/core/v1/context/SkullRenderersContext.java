package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.Function;

/**
 * Register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations.
 */
@FunctionalInterface
public interface SkullRenderersContext {

    /**
     * @param skullBlockType    the skull block type
     * @param skullModelFactory thue skull model factory
     */
    void registerSkullRenderer(SkullBlock.Type skullBlockType, Function<EntityModelSet, SkullModelBase> skullModelFactory);
}
