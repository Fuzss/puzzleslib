package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Predicates;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
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
     * @param renderLayerFactory the new layer factory
     * @param <S>                the entity render state
     * @param <M>                the entity model
     */
    default <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void registerRenderLayer(BiFunction<RenderLayerParent<S, M>, EntityRendererProvider.Context, RenderLayer<S, M>> renderLayerFactory) {
        this.registerRenderLayer(Predicates.alwaysTrue(), renderLayerFactory);
    }

    /**
     * Registers a render layer for a specific entity type.
     *
     * @param entityType         the entity type to register for
     * @param renderLayerFactory the new layer factory
     * @param <S>                the entity render state
     * @param <M>                the entity model
     */
    default <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void registerRenderLayer(EntityType<? extends LivingEntity> entityType, BiFunction<RenderLayerParent<S, M>, EntityRendererProvider.Context, RenderLayer<S, M>> renderLayerFactory) {
        Objects.requireNonNull(entityType, "entity type is null");
        this.registerRenderLayer((EntityType<? extends LivingEntity> entityTypeX) -> {
            return entityTypeX == entityType;
        }, renderLayerFactory);
    }

    /**
     * Registers a render layer.
     *
     * @param filter             the filter for controlling affected entity types
     * @param renderLayerFactory the new layer factory
     * @param <S>                the entity render state
     * @param <M>                the entity model
     */
    <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void registerRenderLayer(Predicate<EntityType<? extends LivingEntity>> filter, BiFunction<RenderLayerParent<S, M>, EntityRendererProvider.Context, RenderLayer<S, M>> renderLayerFactory);
}
