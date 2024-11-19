package fuzs.puzzleslib.api.util.v1;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * A helper class for adapting the new {@link InteractionResult} class from the previous {@code InteractionResult},
 * {@code ItemInteractionResult}, and {@code InteractionResultHolder} implementations.
 */
public final class InteractionResultHelper {

    private InteractionResultHelper() {
        // NO-OP
    }

    /**
     * An abstraction for {@code InteractionResult#SUCCESS}.
     *
     * @return the interaction result
     */
    public static InteractionResult success() {
        return InteractionResult.SUCCESS_SERVER;
    }

    /**
     * An abstraction for {@code InteractionResult#CONSUME}.
     *
     * @return the interaction result
     */
    public static InteractionResult consume() {
        return InteractionResult.CONSUME;
    }

    /**
     * An abstraction for {@code InteractionResult#PASS}.
     *
     * @return the interaction result
     */
    public static InteractionResult pass() {
        return InteractionResult.PASS;
    }

    /**
     * An abstraction for {@code InteractionResult#FAIL}.
     *
     * @return the interaction result
     */
    public static InteractionResult fail() {
        return InteractionResult.FAIL;
    }

    /**
     * An abstraction for {@code InteractionResult::sidedSuccess}.
     *
     * @param isClientSide should the interaction trigger an arm swing on the client-side only
     * @return the interaction result
     */
    public static InteractionResult sidedSuccess(boolean isClientSide) {
        return InteractionResult.SUCCESS;
    }

    /**
     * An abstraction for {@code InteractionResultHolder::success}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResult success(ItemStack itemStack) {
        return InteractionResult.SUCCESS_SERVER.heldItemTransformedTo(itemStack);
    }

    /**
     * An abstraction for {@code InteractionResultHolder::consume}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResult consume(ItemStack itemStack) {
        return InteractionResult.CONSUME;
    }

    /**
     * An abstraction for {@code InteractionResultHolder::pass}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResult pass(ItemStack itemStack) {
        return InteractionResult.PASS;
    }

    /**
     * An abstraction for {@code InteractionResultHolder::fail}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResult fail(ItemStack itemStack) {
        return InteractionResult.FAIL;
    }

    /**
     * An abstraction for {@code InteractionResultHolder::sidedSuccess}.
     *
     * @param itemStack    the item stack resulting from the interaction
     * @param isClientSide should the interaction trigger an arm swing on the client-side only
     * @return the interaction result
     */
    public static InteractionResult sidedSuccess(ItemStack itemStack, boolean isClientSide) {
        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
    }

    /**
     * An abstraction for {@code ItemInteractionResult#PASS_TO_DEFAULT_BLOCK_INTERACTION}.
     *
     * @return the interaction result
     */
    public static InteractionResult passToDefaultBlockInteraction() {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    /**
     * An abstraction for {@code ItemInteractionResult#SKIP_DEFAULT_BLOCK_INTERACTION}.
     *
     * @return the interaction result
     */
    public static InteractionResult skipDefaultBlockInteraction() {
        return InteractionResult.PASS;
    }

    /**
     * An abstraction for {@code InteractionResult::shouldSwing}.
     *
     * @param interactionResult the interaction result
     * @return should the player arm be swung via Player::swing
     */
    public static boolean shouldSwing(InteractionResult interactionResult) {
        // there is no need for distinguishing between client and server swing sources
        return interactionResult instanceof InteractionResult.Success success && success.swingSource() != InteractionResult.SwingSource.NONE;
    }
}
