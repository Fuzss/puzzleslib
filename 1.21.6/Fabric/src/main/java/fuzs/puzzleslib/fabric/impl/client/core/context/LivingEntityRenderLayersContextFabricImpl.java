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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class LivingEntityRenderLayersContextFabricImpl implements LivingEntityRenderLayersContext {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void registerRenderLayer(Predicate<EntityType<? extends LivingEntity>> filter, BiFunction<RenderLayerParent<S, M>, EntityRendererProvider.Context, RenderLayer<S, M>> renderLayerFactory) {
        Objects.requireNonNull(filter, "filter is null");
        Objects.requireNonNull(renderLayerFactory, "render layer factory is null");
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
                    if (filter.test(entityType)) {
                        registrationHelper.register((RenderLayer<S, ? extends EntityModel<S>>) renderLayerFactory.apply(
                                (RenderLayerParent<S, M>) entityRenderer, context));
                    }
                });
    }
}
