package fuzs.puzzleslib.fabric.api.client.event.v1.registry;

import fuzs.puzzleslib.fabric.impl.client.event.SkullRendererRegistryImpl;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.Function;

/**
 * Allows for injecting custom skull type models in
 * {@link net.minecraft.client.renderer.blockentity.SkullBlockRenderer#createModel(EntityModelSet, SkullBlock.Type)}.
 */
public interface SkullRendererRegistry {
    /**
     * The singleton instance of the decorator registry. Use this instance to call the methods in this interface.
     */
    SkullRendererRegistry INSTANCE = new SkullRendererRegistryImpl();

    /**
     * @param skullBlockType    the skull block type
     * @param skullModelFactory the skull model factory
     */
    void register(SkullBlock.Type skullBlockType, Function<EntityModelSet, SkullModelBase> skullModelFactory);
}
