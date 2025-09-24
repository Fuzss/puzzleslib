package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

@FunctionalInterface
public interface RenderNameTagCallback {
    EventInvoker<RenderNameTagCallback> EVENT = EventInvoker.lookup(RenderNameTagCallback.class);

    /**
     * Fires before the name tag of an entity is rendered via
     * {@link EntityRenderer#submitNameTag(EntityRenderState, PoseStack, SubmitNodeCollector, CameraRenderState)}.
     * <p>
     * For {@link net.minecraft.client.renderer.entity.player.AvatarRenderer} this includes not only the name tag itself
     * but also the score text value.
     *
     * @param entityRenderer      the entity renderer instance
     * @param renderState         the entity render state
     * @param poseStack           the pose stack
     * @param submitNodeCollector the submit node collector
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the name tag from rendering</li>
     *         <li>{@link EventResult#PASS PASS} to allow the name tag to render if present</li>
     *         </ul>
     */
    EventResult onRenderNameTag(EntityRenderer<?, ?> entityRenderer, EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector);
}
