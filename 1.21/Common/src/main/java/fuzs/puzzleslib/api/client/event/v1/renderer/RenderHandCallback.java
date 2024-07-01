package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @deprecated see {@link RenderHandEvents}
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface RenderHandCallback {
    EventInvoker<RenderHandCallback> EVENT = EventInvoker.lookup(RenderHandCallback.class);

    /**
     * Called before one of the player's hands is rendered in first-person mode.
     * <p>The event is fired for both hands separately, the current hand can be retrieved from <code>hand</code>.
     * <p>Allows for cancelling rendering of the hand.
     *
     * @param player            the local player instance used for first-person rendering
     * @param interactionHand   the {@link InteractionHand} that is being rendered as part of this event
     * @param itemStack         the {@link ItemStack} held in the hand
     * @param poseStack         the current {@link PoseStack}
     * @param multiBufferSource the current {@link MultiBufferSource}
     * @param combinedLight     packet light the hand is rendered with
     * @param partialTick       current partial tick time
     * @param interpolatedPitch the pitch interpolated for current tick delta from {@link Player#getXRot()}
     * @param swingProgress     the forward swing state of the hand from attacking / mining, originally retrieved from
     *                          {@link Player#getAttackAnim(float)}
     * @param equipProgress     the height the hand is rendered at, changes when switching between hotbar items and
     *                          after triggering the attack cool-down, originally retrieved from
     *                          {@link Player#getAttackStrengthScale(float)}
     * @return {@link EventResult#INTERRUPT} to prevent the specific hand from rendering,
     *         <p>
     *         {@link EventResult#PASS} to allow the hand to render normally
     */
    EventResult onRenderHand(Player player, InteractionHand interactionHand, ItemStack itemStack, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress);
}
