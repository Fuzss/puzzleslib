package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LivingEntityRenderLayersContext;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Deprecated
public final class LivingEntityRenderLayersContextFabricImpl implements LivingEntityRenderLayersContext {

    @SuppressWarnings("unchecked")
    @Override
    public <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(Predicate<EntityType<E>> filter, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
        Objects.requireNonNull(filter, "filter is null");
        Objects.requireNonNull(factory, "render layer factory is null");
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
                    if (filter.test((EntityType<E>) entityType)) {
                        registrationHelper.register(factory.apply((RenderLayerParent<T, M>) entityRenderer, context));
                    }
                });
    }
}
