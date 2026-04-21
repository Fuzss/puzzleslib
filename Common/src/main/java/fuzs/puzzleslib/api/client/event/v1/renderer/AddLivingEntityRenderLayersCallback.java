package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerModelType;

@FunctionalInterface
public interface AddLivingEntityRenderLayersCallback {
    EventInvoker<AddLivingEntityRenderLayersCallback> EVENT = EventInvoker.lookup(AddLivingEntityRenderLayersCallback.class);

    /**
     * Called after entity renderers have been created, and allows for attaching new
     * {@link net.minecraft.client.renderer.entity.layers.RenderLayer RenderLayers}.
     *
     * @param entityType     the entity type
     * @param entityRenderer the entity renderer
     * @param context        the entity renderer provider context
     */
    void addLivingEntityRenderLayers(EntityType<?> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, EntityRendererProvider.Context context);

    /**
     * A helper for identifying the model type of {@link AvatarRenderer}.
     *
     * @param avatarRenderer the avatar renderer
     * @return the model type
     */
    static PlayerModelType getPlayerModelType(AvatarRenderer<?> avatarRenderer) {
        return avatarRenderer.getModel().slim ? PlayerModelType.SLIM : PlayerModelType.WIDE;
    }
}
