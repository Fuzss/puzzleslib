package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.HitResult;

@FunctionalInterface
public interface RenderHighlightCallback {
    EventInvoker<RenderHighlightCallback> EVENT = EventInvoker.lookup(RenderHighlightCallback.class);

    /**
     * Fires before the highlight outline for the current hit result is attempted to be drawn.
     * <p>Vanilla only handles this in case the hit result is {@link HitResult.Type#BLOCK}, but the callback also allows for handling {@link HitResult.Type#ENTITY}.
     *
     * @param levelRenderer     the level renderer instance
     * @param camera            the camera instance
     * @param gameRenderer      the game renderer instance
     * @param hitResult         the hit result to render the highlight outline for, this can be either {@link HitResult.Type#BLOCK} or {@link HitResult.Type#ENTITY}
     * @param tickDelta         partial tick time
     * @param poseStack         current pose stack
     * @param multiBufferSource the buffer source
     * @param level             the current client level
     * @return {@link EventResult#INTERRUPT} to prevent the highlight from rendering, allowing for custom rendering,
     * {@link EventResult#PASS} to allow vanilla outline rendering to happen
     */
    EventResult onRenderHighlight(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, HitResult hitResult, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, ClientLevel level);
}
