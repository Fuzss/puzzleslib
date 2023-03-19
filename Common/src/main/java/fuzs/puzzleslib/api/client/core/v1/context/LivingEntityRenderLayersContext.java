package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;

/**
 * register additional {@link RenderLayer}s for a living entity, supports players like any other entity
 */
@FunctionalInterface
public interface LivingEntityRenderLayersContext {


    /**
     * register the additional layer
     *
     * @param entityType entity type to register for
     * @param factory    the new layer factory
     * @param <E>        the entity type
     * @param <T>        entity type used for the model, should only really be different for players
     * @param <M>        the entity model
     */
    <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory);
}
