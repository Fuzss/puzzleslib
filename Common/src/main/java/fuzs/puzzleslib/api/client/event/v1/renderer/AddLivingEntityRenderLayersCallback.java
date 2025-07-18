package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;

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
     * A helper for identifying the skin model of a {@link PlayerRenderer}.
     *
     * @param playerRenderer the player renderer
     * @return the skin model
     */
    static PlayerSkin.Model getPlayerSkinModel(PlayerRenderer playerRenderer) {
        return playerRenderer.getModel().slim ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE;
    }
}
