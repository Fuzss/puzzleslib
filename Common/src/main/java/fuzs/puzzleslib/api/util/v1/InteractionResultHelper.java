package fuzs.puzzleslib.api.util.v1;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * A helper class for adapting the new {@link InteractionResult} class from the previous {@code InteractionResult},
 * {@code ItemInteractionResult}, and {@code InteractionResultHolder} implementations.
 */
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
    public static final ItemInteractionResult PASS_TO_DEFAULT_BLOCK_INTERACTION = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    /**
     * An abstraction for {@code ItemInteractionResult#SKIP_DEFAULT_BLOCK_INTERACTION}.
     */
    public static final ItemInteractionResult SKIP_DEFAULT_BLOCK_INTERACTION = ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

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
        return interactionResult.shouldSwing();
    }

    /**
     * An abstraction for {@code InteractionResult::indicateItemUse}.
     *
     * @param interactionResult the interaction result
     * @return should {@link net.minecraft.stats.Stats#ITEM_USED} be awarded
     */
    public static boolean indicateItemUse(InteractionResult interactionResult) {
        return interactionResult.indicateItemUse();
    }

    /**
     * An abstraction for {@code InteractionResult::sidedSuccess}.
     *
     * @param isClientSide should the interaction trigger an arm swing on the client-side only
     * @return the interaction result
     */
    public static InteractionResult sidedSuccess(boolean isClientSide) {
        return InteractionResult.sidedSuccess(isClientSide);
    }

    /**
     * An abstraction for {@code InteractionResultHolder::getObject}.
     *
     * @param interactionResult the interaction result
     * @return the new held item stack, possibly {@link ItemStack#EMPTY} if not available
     */
    public static ItemStack getObject(InteractionResultHolder<ItemStack> interactionResult) {
        return interactionResult.getObject();
    }

    /**
     * An abstraction for {@code InteractionResultHolder::success}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResultHolder<ItemStack> success(ItemStack itemStack) {
        return InteractionResultHolder.success(itemStack);
    }

    /**
     * An abstraction for {@code InteractionResultHolder::consume}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResultHolder<ItemStack> consume(ItemStack itemStack) {
        return InteractionResultHolder.consume(itemStack);
    }

    /**
     * An abstraction for {@code InteractionResultHolder::pass}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResultHolder<ItemStack> pass(ItemStack itemStack) {
        return InteractionResultHolder.pass(itemStack);
    }

    /**
     * An abstraction for {@code InteractionResultHolder::fail}.
     *
     * @param itemStack the item stack resulting from the interaction
     * @return the interaction result
     */
    public static InteractionResultHolder<ItemStack> fail(ItemStack itemStack) {
        return InteractionResultHolder.fail(itemStack);
    }

    /**
     * An abstraction for {@code InteractionResultHolder::sidedSuccess}.
     *
     * @param itemStack    the item stack resulting from the interaction
     * @param isClientSide should the interaction trigger an arm swing on the client-side only
     * @return the interaction result
     */
    public static InteractionResultHolder<ItemStack> sidedSuccess(ItemStack itemStack, boolean isClientSide) {
        return InteractionResultHolder.sidedSuccess(itemStack, isClientSide);
    }
}
