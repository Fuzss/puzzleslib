package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;

public record BlockEntityRenderersContextNeoForgeImpl(EntityRenderersEvent.RegisterRenderers event) implements BlockEntityRenderersContext {

    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRendererProvider) {
        Objects.requireNonNull(blockEntityType, "block entity type is null");
        Objects.requireNonNull(blockEntityRendererProvider, "block entity renderer provider is null");
        this.event.registerBlockEntityRenderer(blockEntityType, blockEntityRendererProvider);
    }
}
