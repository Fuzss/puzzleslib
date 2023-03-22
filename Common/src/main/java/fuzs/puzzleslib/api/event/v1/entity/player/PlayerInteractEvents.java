package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class PlayerInteractEvents {
    public static final EventInvoker<UseBlock> USE_BLOCK = EventInvoker.lookup(UseBlock.class);
    public static final EventInvoker<UseItem> USE_ITEM = EventInvoker.lookup(UseItem.class);

    private PlayerInteractEvents() {

    }

    public interface UseBlock {

        /**
         * This event is fired on both sides before the player triggers {@link net.minecraft.world.level.block.Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} by right-clicking.
         *
         * @param player    the player interacting with the block
         * @param level     the level the interaction is happening in
         * @param hand      the hand <code>player</code> is using to interfact
         * @param hitResult the targeted block
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         * {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResult> onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    public interface UseItem {

        /**
         * This event is fired on both sides before the player triggers {@link Item#use(Level, Player, InteractionHand)} by right-clicking an item.
         *
         * @param player the player using the item
         * @param level  the level the interaction is happening in
         * @param hand   the hand <code>player</code> is holding the item
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         * {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResultHolder<ItemStack>> onUseItem(Player player, Level level, InteractionHand hand);
    }
}
