package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;

public record EntityRenderersContextNeoForgeImpl(EntityRenderersEvent.RegisterRenderers event) implements EntityRenderersContext {

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(entityRendererProvider, "entity renderer provider is null");
        this.event.registerEntityRenderer(entityType, entityRendererProvider);
    }
}
