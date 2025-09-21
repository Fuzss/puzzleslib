package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface RenderNameTagCallback {
    EventInvoker<RenderNameTagCallback> EVENT = EventInvoker.lookup(RenderNameTagCallback.class);

    /**
     * Fires before the name tag of an entity is rendered.
     * <p>
     * The name tag must be forced to render via {@link ExtractRenderStateCallback} for the callback to run.
     *
     * @param renderState    the entity render state
     * @param component      the entity display name retrieved from {@link EntityRenderer#getNameTag(Entity)}
     * @param entityRenderer the entity renderer instance
     * @param poseStack      the pose stack
     * @param bufferSource   the buffer source
     * @param packedLight    the light values the entity is rendered with
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT} to prevent the name tag from rendering</li>
     *         <li>{@link EventResult#PASS} to allow the name tag to render if present</li>
     *         </ul>
     */
    EventResult onRenderNameTag(EntityRenderState renderState, Component component, EntityRenderer<?, ?> entityRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);
}
