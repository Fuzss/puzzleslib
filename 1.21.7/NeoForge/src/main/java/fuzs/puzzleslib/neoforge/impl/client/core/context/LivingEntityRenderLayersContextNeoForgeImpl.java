package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.client.core.v1.context.LivingEntityRenderLayersContext;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public record LivingEntityRenderLayersContextNeoForgeImpl(EntityRenderersEvent.AddLayers event) implements LivingEntityRenderLayersContext {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void registerRenderLayer(Predicate<EntityType<? extends LivingEntity>> entityTypeFilter, BiFunction<RenderLayerParent<S, M>, EntityRendererProvider.Context, @Nullable RenderLayer<S, M>> renderLayerFactory) {
        Objects.requireNonNull(entityTypeFilter, "filter is null");
        Objects.requireNonNull(renderLayerFactory, "render layer factory is null");
        EntityRendererProvider.Context context = this.event.getContext();
        for (EntityType<?> entityType : this.getEntityTypes()) {
            if (entityTypeFilter.test((EntityType<? extends LivingEntity>) entityType)) {
                for (EntityRenderer<?, ?> entityRenderer : this.getEntityRenderer(entityType)) {
                    if (entityRenderer instanceof LivingEntityRenderer<?, ?, ?>) {
                        LivingEntityRenderer<?, S, M> livingEntityRenderer = (LivingEntityRenderer<?, S, M>) entityRenderer;
                        RenderLayer<S, M> renderLayer = renderLayerFactory.apply(livingEntityRenderer, context);
                        if (renderLayer != null) {
                            livingEntityRenderer.addLayer(renderLayer);
                        }
                    }
                }
            }
        }
    }

    private Collection<EntityType<?>> getEntityTypes() {
        Collection<EntityType<?>> entityTypes = new HashSet<>(this.event.getEntityTypes());
        entityTypes.add(EntityType.PLAYER);
        return entityTypes;
    }

    private Collection<EntityRenderer<?, ?>> getEntityRenderer(EntityType<?> entityType) {
        if (entityType == EntityType.PLAYER) {
            ImmutableSet.Builder<EntityRenderer<?, ?>> builder = ImmutableSet.builder();
            for (PlayerSkin.Model model : this.event.getSkins()) {
                EntityRenderer<? extends Player, ?> entityRenderer = this.event.getSkin(model);
                if (entityRenderer != null) {
                    builder.add(entityRenderer);
                }
            }
            return builder.build();
        } else {
            return Collections.singleton(this.event.getRenderer(entityType));
        }
    }
}
