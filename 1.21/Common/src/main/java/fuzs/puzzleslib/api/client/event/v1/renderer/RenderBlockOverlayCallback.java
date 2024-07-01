package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RenderBlockOverlayCallback {
    EventInvoker<RenderBlockOverlayCallback> EVENT = EventInvoker.lookup(RenderBlockOverlayCallback.class);

    /**
     * Called before a block overlay is rendered on the screen.
     * This is the case for the flame overlay on the screen bottom while the player is burning,
     * for the water overlay while the player is diving in water,
     * and for any solid block when the player is inside of it and the vision is obstructed.
     *
     * @param player     the local client player the overlay is rendering for
     * @param poseStack  the current pose stack
     * @param blockState the block state the overlay originates from, will be {@link Block#defaultBlockState()} for fire and water overlays
     * @return {@link EventResult#INTERRUPT} to prevent the overlay from rendering,
     * {@link EventResult#PASS} to allow the overlay to render
     */
    EventResult onRenderBlockOverlay(LocalPlayer player, PoseStack poseStack, @Nullable BlockState blockState);
}
