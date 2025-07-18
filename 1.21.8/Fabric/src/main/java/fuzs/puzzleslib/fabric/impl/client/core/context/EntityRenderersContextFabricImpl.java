package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public final class EntityRenderersContextFabricImpl implements EntityRenderersContext {

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(entityRendererProvider, "entity renderer provider is null");
        EntityRendererRegistry.register(entityType, entityRendererProvider);
    }
}
