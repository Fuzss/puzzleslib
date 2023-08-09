package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * register a renderer for a block entity
 */
@FunctionalInterface
public interface BlockEntityRenderersContext {

    /**
     * registers an {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} for a given block entity
     *
     * @param blockEntityType             block entity type token to render for
     * @param blockEntityRendererProvider block entity renderer provider
     * @param <T>                         type of entity
     */
    <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider);
}
