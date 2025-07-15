package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LivingEntityRenderLayersContext;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class LivingEntityRenderLayersContextFabricImpl implements LivingEntityRenderLayersContext {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void registerRenderLayer(Predicate<EntityType<? extends LivingEntity>> entityTypeFilter, BiFunction<RenderLayerParent<S, M>, EntityRendererProvider.Context, @Nullable RenderLayer<S, M>> renderLayerFactory) {
        Objects.requireNonNull(entityTypeFilter, "filter is null");
        Objects.requireNonNull(renderLayerFactory, "render layer factory is null");
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
            if (entityTypeFilter.test(entityType)) {
                LivingEntityRenderer<?, S, M> livingEntityRenderer = (LivingEntityRenderer<?, S, M>) entityRenderer;
                RenderLayer<S, M> renderLayer = renderLayerFactory.apply(livingEntityRenderer, context);
                if (renderLayer != null) {
                    registrationHelper.register((RenderLayer<S, ? extends EntityModel<S>>) renderLayer);
                }
            }
        });
    }
}
