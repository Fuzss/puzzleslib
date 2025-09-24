package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class RenderHandEvents {
    public static final EventInvoker<MainHand> MAIN_HAND = EventInvoker.lookup(MainHand.class);
    public static final EventInvoker<OffHand> OFF_HAND = EventInvoker.lookup(OffHand.class);
    public static final EventInvoker<MainHand> BOTH = (EventPhase phase, MainHand callback) -> {
        MAIN_HAND.register(phase, callback);
        OFF_HAND.register(phase, callback::onRenderMainHand);
    };

    private RenderHandEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface MainHand {

        /**
         * Called before the player's {@link net.minecraft.world.InteractionHand#MAIN_HAND} is rendered in first-person
         * mode.
         * <p>
         * Allows for cancelling rendering of the hand.
         *
         * @param itemInHandRenderer  the item in hand renderer instance
         * @param interactionHand     the player hand
         * @param player              the local player instance used for first-person rendering
         * @param humanoidArm         the screen side the arm is rendering at
         * @param itemStack           the {@link ItemStack} held in the hand
         * @param poseStack           the current {@link PoseStack}
         * @param submitNodeCollector the current {@link SubmitNodeCollector}
         * @param combinedLight       packet light the hand is rendered with
         * @param partialTick         current partial tick time
         * @param interpolatedPitch   the pitch interpolated for current tick delta from {@link Player#getXRot()}
         * @param swingProgress       the forward swing state of the hand from attacking / mining, originally retrieved
         *                            from {@link Player#getAttackAnim(float)}
         * @param equipProgress       the height the hand is rendered at, changes when switching between hotbar items
         *                            and after triggering the attack cool-down, originally retrieved from
         *                            {@link Player#getAttackStrengthScale(float)}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the hand from rendering</li>
         *         <li>{@link EventResult#PASS PASS} to allow the hand to render normally</li>
         *         </ul>
         */
        EventResult onRenderMainHand(ItemInHandRenderer itemInHandRenderer, InteractionHand interactionHand, AbstractClientPlayer player, HumanoidArm humanoidArm, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int combinedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress);
    }

    @FunctionalInterface
    public interface OffHand {

        /**
         * Called before the player's {@link net.minecraft.world.InteractionHand#OFF_HAND} is rendered in first-person
         * mode.
         * <p>
         * Allows for cancelling rendering of the hand.
         *
         * @param itemInHandRenderer  the item in hand renderer instance
         * @param interactionHand     the player hand
         * @param player              the local player instance used for first-person rendering
         * @param humanoidArm         the screen side the arm is rendering at
         * @param itemStack           the {@link ItemStack} held in the hand
         * @param poseStack           the current {@link PoseStack}
         * @param submitNodeCollector the current {@link SubmitNodeCollector}
         * @param combinedLight       packet light the hand is rendered with
         * @param partialTick         current partial tick time
         * @param interpolatedPitch   the pitch interpolated for current tick delta from {@link Player#getXRot()}
         * @param swingProgress       the forward swing state of the hand from attacking / mining, originally retrieved
         *                            from {@link Player#getAttackAnim(float)}
         * @param equipProgress       the height the hand is rendered at, changes when switching between hotbar items
         *                            and after triggering the attack cool-down, originally retrieved from
         *                            {@link Player#getAttackStrengthScale(float)}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the hand from rendering</li>
         *         <li>{@link EventResult#PASS PASS} to allow the hand to render normally</li>
         *         </ul>
         */
        EventResult onRenderOffHand(ItemInHandRenderer itemInHandRenderer, InteractionHand interactionHand, AbstractClientPlayer player, HumanoidArm humanoidArm, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int combinedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress);
    }
}
