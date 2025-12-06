package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

@FunctionalInterface
public interface SubmitNameTagCallback {
    EventInvoker<SubmitNameTagCallback> EVENT = EventInvoker.lookup(SubmitNameTagCallback.class);

    /**
     * Fires before the name tag of an entity is submitted.
     * <p>
     * For {@link net.minecraft.client.renderer.entity.player.AvatarRenderer} this includes not only the name tag itself
     * but also the score text value.
     *
     * @param entityRenderer    the entity renderer instance
     * @param renderState       the entity render state
     * @param poseStack         the pose stack
     * @param nodeCollector     the submit node collector
     * @param cameraRenderState the camera render state
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the name tag from rendering</li>
     *         <li>{@link EventResult#PASS PASS} to allow the name tag to render if present</li>
     *         </ul>
     */
    EventResult onSubmitNameTag(EntityRenderer<?, ?> entityRenderer, EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState);
}
