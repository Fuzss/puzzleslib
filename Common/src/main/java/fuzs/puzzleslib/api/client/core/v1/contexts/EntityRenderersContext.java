package fuzs.puzzleslib.api.client.core.v1.contexts;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * register a renderer for an entity
 */
@FunctionalInterface
public interface EntityRenderersContext {

    /**
     * registers an {@link net.minecraft.client.renderer.entity.EntityRenderer} for a given entity
     *
     * @param entityType             entity type token to render for
     * @param entityRendererProvider entity renderer provider
     * @param <T>                    type of entity
     */
    <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider);
}
