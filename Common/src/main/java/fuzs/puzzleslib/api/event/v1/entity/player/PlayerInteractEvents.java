package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PlayerInteractEvents {
    public static final EventInvoker<UseItem> USE_ITEM = EventInvoker.lookup(UseItem.class);

    private PlayerInteractEvents() {

    }

    @FunctionalInterface
    public interface UseItem {

        /**
         * This event is fired on both sides before the player triggers {@link Item#use(Level, Player, InteractionHand)} by right-clicking an item.
         *
         * @param player the player using the item
         * @param level the level the interaction is happening in
         * @param hand the hand <code>player</code> is holding the item
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         *         {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResultHolder<ItemStack>> onRightClickItem(Player player, Level level, InteractionHand hand);
    }
}
