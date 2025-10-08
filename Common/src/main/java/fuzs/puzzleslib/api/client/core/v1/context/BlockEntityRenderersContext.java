package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Register a renderer for a block entity.
 */
@FunctionalInterface
public interface BlockEntityRenderersContext {

    /**
     * Registers an {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} for a given block entity.
     *
     * @param blockEntityType             the block entity type token to render for
     * @param blockEntityRendererProvider the block entity renderer provider
     * @param <T>                         the type of block entity
     */
    <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRendererProvider);
}
