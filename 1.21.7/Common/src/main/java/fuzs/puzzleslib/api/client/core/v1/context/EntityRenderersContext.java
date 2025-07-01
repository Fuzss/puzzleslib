package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@FunctionalInterface
public interface EntityRenderersContext {

    /**
     * Register an {@link net.minecraft.client.renderer.entity.EntityRenderer} for an entity.
     *
     * @param entityType             the entity type
     * @param entityRendererProvider the entity renderer provider
     * @param <T>                    the type of entity
     */
    <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider);
}
