package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PlayerEvents {
    public static final EventInvoker<ItemPickup> ITEM_PICKUP = EventInvoker.lookup(ItemPickup.class);

    private PlayerEvents() {

    }

    public interface ItemPickup {

        /**
         * Called when the player picks up an {@link ItemEntity} from the ground after the {@link ItemStack} has been added to the player inventory.
         * <p>This events main purpose is to notify that the item pickup has happened.
         *
         * @param player     the player that touched <code>itemEntity</code>, picking up <code>stack</code> in the process
         * @param itemEntity the {@link ItemEntity} that was touched
         * @param stack      a stack copy containing the amount that was added to the player inventory, usually the full stack from <code>itemEntity</code>,
         *                   but can also be an empty stack in case the item pick-up was forced without considering the player inventory via {@link ItemTouchCallback}
         */
        void onItemPickup(Player player, ItemEntity itemEntity, ItemStack stack);
    }
}
