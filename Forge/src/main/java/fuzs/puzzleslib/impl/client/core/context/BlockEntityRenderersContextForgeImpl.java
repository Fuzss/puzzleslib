package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Objects;

public record BlockEntityRenderersContextForgeImpl(
        BlockEntityRenderersContext context) implements BlockEntityRenderersContext {

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
        Objects.requireNonNull(blockEntityType, "block entity type is null");
        Objects.requireNonNull(blockEntityRendererProvider, "block entity renderer provider is null");
        this.context.registerBlockEntityRenderer(blockEntityType, blockEntityRendererProvider);
    }
}
