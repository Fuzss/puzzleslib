package fuzs.puzzleslib.api.util.v1;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * A helper class for adapting the new {@link InteractionResult} class from the previous {@code InteractionResult},
 * {@code ItemInteractionResult}, and {@code InteractionResultHolder} implementations.
 */
@Deprecated
public final class InteractionResultHelper {
    /**
     * An abstraction for {@code InteractionResult#SUCCESS}.
     */
    public static final InteractionResult SUCCESS = InteractionResult.SUCCESS;
    /**
     * An abstraction for {@code InteractionResult#CONSUME}.
     */
    public static final InteractionResult CONSUME = InteractionResult.CONSUME;
    /**
     * An abstraction for {@code InteractionResult#PASS}.
     */
    public static final InteractionResult PASS = InteractionResult.PASS;
    /**
     * An abstraction for {@code InteractionResult#FAIL}.
     */
    public static final InteractionResult FAIL = InteractionResult.FAIL;
    /**
     * An abstraction for {@code ItemInteractionResult#PASS_TO_DEFAULT_BLOCK_INTERACTION}.
     */
    public static final InteractionResult PASS_TO_DEFAULT_BLOCK_INTERACTION = InteractionResult.TRY_WITH_EMPTY_HAND;
    /**
     * An abstraction for {@code ItemInteractionResult#SKIP_DEFAULT_BLOCK_INTERACTION}.
     */
    public static final InteractionResult SKIP_DEFAULT_BLOCK_INTERACTION = InteractionResult.PASS;

    private InteractionResultHelper() {
        // NO-OP
    }

    /**
     * An abstraction for {@code InteractionResult::consumesAction}.
     *
     * @param interactionResult the interaction result
     * @return should any further attempts at an interaction be prevented
     */
    public static boolean consumesAction(InteractionResult interactionResult) {
        return interactionResult.consumesAction();
    }

    /**
     * An abstraction for {@code InteractionResult::shouldSwing}.
     *
     * @param interactionResult the interaction result
     * @return should the player arm be swung via Player::swing
     */
    public static boolean shouldSwing(InteractionResult interactionResult) {
        // there is no need for distinguishing between client and server swing sources
        return interactionResult instanceof InteractionResult.Success success &&
                success.swingSource() != InteractionResult.SwingSource.NONE;
    }

    /**
     * An abstraction for {@code InteractionResult::indicateItemUse}.
     *
     * @param interactionResult the interaction result
     * @return should {@link net.minecraft.stats.Stats#ITEM_USED} be awarded
     */
    public static boolean indicateItemUse(InteractionResult interactionResult) {
        return interactionResult instanceof InteractionResult.Success success &&
                success.itemContext().wasItemInteraction();
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
     * An abstraction for {@code InteractionResultHolder::getObject}.
     *
     * @param interactionResult the interaction result
     * @return the new held item stack, possibly {@link ItemStack#EMPTY} if not available
     */
    public static ItemStack getObject(InteractionResult interactionResult) {
        return interactionResult instanceof InteractionResult.Success success &&
                success.heldItemTransformedTo() != null ? success.heldItemTransformedTo() : ItemStack.EMPTY;
    }

    /**
     * An abstraction for {@code InteractionResultHolder::success}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResult success(ItemStack itemStack) {
        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
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
}
