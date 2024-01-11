package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LivingEntityRenderLayersContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.BiFunction;

public record LivingEntityRenderLayersContextForgeImpl(
        EntityRenderersEvent.AddLayers evt) implements LivingEntityRenderLayersContext {

    @SuppressWarnings("unchecked")
    @Override
    public <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(factory, "render layer factory is null");
        if (entityType == EntityType.PLAYER) {
            this.evt.getSkins().stream().map(this.evt::getSkin).filter(Objects::nonNull).map(entityRenderer -> ((LivingEntityRenderer<T, M>) entityRenderer)).forEach(entityRenderer -> {
                this.actuallyRegisterRenderLayer(entityRenderer, factory);
            });
        } else {
            LivingEntityRenderer<T, M> entityRenderer = (LivingEntityRenderer<T, M>) this.evt.getRenderer(entityType);
            Objects.requireNonNull(entityRenderer, "entity renderer for %s is null".formatted(ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString()));
            this.actuallyRegisterRenderLayer(entityRenderer, factory);
        }
    }

    private <T extends LivingEntity, M extends EntityModel<T>> void actuallyRegisterRenderLayer(LivingEntityRenderer<T, M> entityRenderer, BiFunction<RenderLayerParent<T, M>, EntityRendererProvider.Context, RenderLayer<T, M>> factory) {
        Minecraft minecraft = Minecraft.getInstance();
        // not sure if there's a way for getting the reload manager that's actually reloading this currently, let's just hope we never need it here
        EntityRendererProvider.Context context = new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.getEntityRenderDispatcher().getItemInHandRenderer(), null, this.evt.getEntityModels(), minecraft.font);
        entityRenderer.addLayer(factory.apply(entityRenderer, context));
    }
}
