package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.Function;

/**
 * Register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations.
 */
public interface SkullRenderersContext {

    /**
     * @param skullBlockType    the skull block type
     * @param textureLocation   the texture location, usually for the corresponding entity
     * @param skullModelFactory the skull model factory
     */
    void registerSkullRenderer(SkullBlock.Type skullBlockType, Identifier textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory);
}
