package fuzs.puzzleslib.fabric.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public final class FabricRendererEvents {
    /**
     * Fires during construction of {@link EntityRenderState}, allows control over the set name tag.
     */
    public static final Event<RenderNameTagEvents.Allow> ALLOW_NAME_TAG = FabricEventFactory.createResult(
            RenderNameTagEvents.Allow.class);
    /**
     * Fires before the name tag of an entity is rendered.
     */
    public static final Event<RenderNameTagEvents.Render> RENDER_NAME_TAG = FabricEventFactory.createResult(
            RenderNameTagEvents.Render.class);
    /**
     * Called before a living entity model is rendered, allows for applying transformations to the {@link PoseStack}, or
     * for completely taking over rendering as a whole.
     */
    public static final Event<RenderLivingEvents.Before> BEFORE_RENDER_LIVING = FabricEventFactory.createResult(
            RenderLivingEvents.Before.class);
    /**
     * Called after a living entity model is rendered, allows for cleaning up transformations applied to the
     * {@link PoseStack}.
     */
    public static final Event<RenderLivingEvents.After> AFTER_RENDER_LIVING = FabricEventFactory.create(
            RenderLivingEvents.After.class);
    /**
     * Called before the player model is rendered, allows for applying transformations to the {@link PoseStack}, or for
     * completely taking over rendering as a whole.
     */
    public static final Event<RenderPlayerEvents.Before> BEFORE_RENDER_PLAYER = FabricEventFactory.createResult(
            RenderPlayerEvents.Before.class);
    /**
     * Called after the player model is rendered, allows for cleaning up transformations applied to the
     * {@link PoseStack}.
     */
    public static final Event<RenderPlayerEvents.After> AFTER_RENDER_PLAYER = FabricEventFactory.create(
            RenderPlayerEvents.After.class);
    /**
     * Called before the player's main hand is rendered in first-person mode.
     */
    public static final Event<RenderHandEvents.MainHand> RENDER_MAIN_HAND = FabricEventFactory.createResult(
            RenderHandEvents.MainHand.class);
    /**
     * Called before the player's off-hand is rendered in first-person mode.
     */
    public static final Event<RenderHandEvents.OffHand> RENDER_OFF_HAND = FabricEventFactory.createResult(
            RenderHandEvents.OffHand.class);
    /**
     * Runs before camera angle setup is done, allows for additional control over roll (which vanilla itself does not
     * support) in addition to pitch and yaw.
     */
    public static final Event<ComputeCameraAnglesCallback> COMPUTE_CAMERA_ANGLES = FabricEventFactory.create(
            ComputeCameraAnglesCallback.class);
    /**
     * Called before a block overlay is rendered on the screen.
     */
    public static final Event<RenderBlockOverlayCallback> RENDER_BLOCK_OVERLAY = FabricEventFactory.createResult(
            RenderBlockOverlayCallback.class);
    /**
     * Called after the fog color is calculated from the current block overlay or biome. Allows for modifying the
     * color.
     */
    public static final Event<FogEvents.ComputeColor> COMPUTE_FOG_COLOR = FabricEventFactory.create(
            FogEvents.ComputeColor.class);
    /**
     * Called before fog is rendered, allows for controlling fog start and end distance.
     */
    public static final Event<FogEvents.Render> RENDER_FOG = FabricEventFactory.create(FogEvents.Render.class);
    /**
     * Fires before the game and level are rendered in {@link GameRenderer#render(DeltaTracker, boolean)}.
     */
    public static final Event<GameRenderEvents.Before> BEFORE_GAME_RENDER = FabricEventFactory.create(
            GameRenderEvents.Before.class);
    /**
     * Fires after the game and level are rendered in {@link GameRenderer#render(DeltaTracker, boolean)}.
     */
    public static final Event<GameRenderEvents.After> AFTER_GAME_RENDER = FabricEventFactory.create(
            GameRenderEvents.After.class);
    /**
     * Runs after field of view is calculated, based on the game setting, but before in-game effects such as nausea are
     * applied.
     */
    public static final Event<ComputeFieldOfViewCallback> COMPUTE_FIELD_OF_VIEW = FabricEventFactory.create(
            ComputeFieldOfViewCallback.class);

    private FabricRendererEvents() {
        // NO-OP
    }
}
