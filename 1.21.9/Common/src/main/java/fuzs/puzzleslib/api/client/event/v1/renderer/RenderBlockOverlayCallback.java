package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface RenderBlockOverlayCallback {
    EventInvoker<RenderBlockOverlayCallback> EVENT = EventInvoker.lookup(RenderBlockOverlayCallback.class);

    /**
     * Called before a block overlay is rendered on the screen. In vanilla this is the case for:
     * <ul>
     *     <li>the flame overlay on the screen bottom while the player is burning</li>
     *     <li>the water overlay while the player is diving in water</li>
     *     <li>any solid block when the player is inside of it and the vision is obstructed</li>
     * </ul>
     *
     * @param player       the local client player the overlay is rendering for
     * @param poseStack    the current pose stack
     * @param bufferSource the buffer source
     * @param blockState   the block state the overlay originates from, will be {@link Block#defaultBlockState()} for
     *                     fire and water overlays
     * @return <ul>
     *                                     <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the overlay from rendering</li>
     *                                     <li>{@link EventResult#PASS PASS} to allow the overlay to render</li>
     *                                 </ul>
     */
    EventResult onRenderBlockOverlay(LocalPlayer player, PoseStack poseStack, MultiBufferSource bufferSource, BlockState blockState);
}
