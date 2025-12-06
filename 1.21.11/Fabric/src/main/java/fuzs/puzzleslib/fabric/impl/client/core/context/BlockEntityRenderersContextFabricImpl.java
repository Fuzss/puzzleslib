package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Objects;

public final class BlockEntityRenderersContextFabricImpl implements BlockEntityRenderersContext {

    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRendererProvider) {
        Objects.requireNonNull(blockEntityType, "block entity type is null");
        Objects.requireNonNull(blockEntityRendererProvider, "block entity renderer provider is null");
        BlockEntityRenderers.register(blockEntityType, blockEntityRendererProvider);
    }
}
