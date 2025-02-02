package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

/**
 * @deprecated {@link RenderLivingEvents} is functionally equivalent now
 */
@Deprecated
public final class RenderPlayerEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private RenderPlayerEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before the player model is rendered, allows for applying transformations to the {@link PoseStack}, or
         * for completely taking over rendering as a whole.
         *
         * @param renderState       the player state that is rendering
         * @param playerRenderer    the used {@link PlayerRenderer} instance
         * @param partialTick       current partial tick time
         * @param poseStack         the current {@link PoseStack}
         * @param multiBufferSource the current {@link MultiBufferSource}
         * @param packedLight       packet light the entity is rendered with
         * @return {@link EventResult#INTERRUPT} to prevent the player model from rendering, this allows for taking over
         *         complete player rendering,
         *         <p>
         *         {@link EventResult#PASS} to allow the player model to render
         */
        EventResult onBeforeRenderPlayer(PlayerRenderState renderState, PlayerRenderer playerRenderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after the player model is rendered, allows for cleaning up transformations applied to the
         * {@link PoseStack}.
         *
         * @param renderState       the player state that is rendering
         * @param playerRenderer    the used {@link PlayerRenderer} instance
         * @param partialTick       current partial tick time
         * @param poseStack         the current {@link PoseStack}
         * @param multiBufferSource the current {@link MultiBufferSource}
         * @param packedLight       packet light the entity is rendered with
         */
        void onAfterRenderPlayer(PlayerRenderState renderState, PlayerRenderer playerRenderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight);
    }
}
