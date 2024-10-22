package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Predicates;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Register additional {@link RenderLayer RenderLayer} for any living entity, including players.
 */
@FunctionalInterface
public interface LivingEntityRenderLayersContext {

    /**
     * Registers a render layer for all living entities.
     *
     * @param factory the new layer factory
     * @param <E>     the entity type
     * @param <T>     the entity type used for the model
     * @param <M>     the entity model
     */
    default <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
        this.registerRenderLayer(Predicates.alwaysTrue(), factory);
    }

    /**
     * Registers a render layer for a specific entity type.
     *
     * @param entityType the entity type to register for
     * @param factory    the new layer factory
     * @param <E>        the entity type
     * @param <T>        the entity type used for the model
     * @param <M>        the entity model
     */
    default <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
        Objects.requireNonNull(entityType, "entity type is null");
        this.registerRenderLayer((EntityType<T> entityTypeX) -> {
            return entityTypeX == entityType;
        }, factory);
    }

    /**
     * Registers a render layer.
     *
     * @param filter  the filter for controlling affected entity types
     * @param factory the new layer factory
     * @param <E>     the entity type
     * @param <T>     the entity type used for the model
     * @param <M>     the entity model
     */
    <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(Predicate<EntityType<E>> filter, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory);
}
