package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public final class RenderNameTagEvents {
    @Deprecated(forRemoval = true)
    public static final EventInvoker<Allow> ALLOW = EventInvoker.lookup(Allow.class);
    public static final EventInvoker<Render> RENDER = EventInvoker.lookup(Render.class);

    private RenderNameTagEvents() {
        // NO-OP
    }

    /**
     * @deprecated {@link ExtractRenderStateCallbackV2} offers the same functionality
     */
    @Deprecated(forRemoval = true)
    public interface Allow {

        /**
         * Fires during construction of {@link EntityRenderState}, allows control over the set name tag.
         *
         * @param entity         the entity
         * @param renderState    the entity render state
         * @param content        the entity display name retrieved from {@link EntityRenderer#getNameTag(Entity)}
         * @param entityRenderer the entity renderer instance
         * @param partialTick    the partial tick time
         * @return <ul>
         *         <li>{@link EventResult#ALLOW} forces the name tag to be set</li>
         *         <li>{@link EventResult#DENY} to prevent any name tag from being set</li>
         *         <li>{@link EventResult#PASS} to let vanilla checks required for the name tag to render continue</li>
         *         </ul>
         */
        EventResult onAllowNameTag(Entity entity, EntityRenderState renderState, Component content, EntityRenderer<?, ?> entityRenderer, float partialTick);
    }

    public interface Render {

        /**
         * Fires before the name tag of an entity is rendered.
         * <p>
         * TODO remove partial tick parameter
         *
         * @param renderState    the entity render state
         * @param component        the entity display name retrieved from {@link EntityRenderer#getNameTag(Entity)}
         * @param entityRenderer the entity renderer instance
         * @param poseStack      the pose stack
         * @param bufferSource   the buffer source
         * @param packedLight    the light values the entity is rendered with
         * @param partialTick    the partial tick time
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT} to prevent the name tag from rendering</li>
         *         <li>{@link EventResult#PASS} to allow the name tag to render if present</li>
         *         </ul>
         */
        EventResult onRenderNameTag(EntityRenderState renderState, Component component, EntityRenderer<?, ?> entityRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick);
    }
}
