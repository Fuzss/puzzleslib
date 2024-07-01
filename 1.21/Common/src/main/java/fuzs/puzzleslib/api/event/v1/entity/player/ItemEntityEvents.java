package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemEntityEvents {
    public static final EventInvoker<Toss> TOSS = EventInvoker.lookup(Toss.class);
    public static final EventInvoker<Touch> TOUCH = EventInvoker.lookup(Touch.class);
    public static final EventInvoker<Pickup> PICKUP = EventInvoker.lookup(Pickup.class);

    private ItemEntityEvents() {

    }

    @FunctionalInterface
    public interface Touch {

        /**
         * Called when a player touches an {@link ItemEntity} laying on the ground.
         *
         * @param player     the player touching the item entity on the ground
         * @param itemEntity the {@link ItemEntity} that is being touched
         * @return {@link EventResult#ALLOW} to force the item to be removed without there being any attempt at adding to the player inventory,
         * {@link EventResult#DENY} to prevent the item from being picked up,
         * {@link EventResult#PASS} to allow vanilla behavior to proceed where the item will be added to the player inventory, staying on the ground if that fails
         */
        EventResult onItemTouch(Player player, ItemEntity itemEntity);
    }

    @FunctionalInterface
    public interface Toss {

        /**
         * Called when an item is tossed from the player inventory, either by pressing 'Q' or by clicking an item stack outside a container screen.
         * <p>This callback can be cancelled so no item entity is added to the level, the item will be lost in that case as it has already been removed from the player inventory.
         *
         * @param player     the player tossing the item stack
         * @param itemEntity item entity containing the item stack being tossed, not added to the level yet
         * @return {@link EventResult#INTERRUPT} to prevent the item from being tossed, nothing will be added to the world and the stack will be lost,
         * {@link EventResult#PASS} to allow the stack to be tossed from the player inventory as usual
         */
        EventResult onItemToss(Player player, ItemEntity itemEntity);
    }

    @FunctionalInterface
    public interface Pickup {

        /**
         * Called when the player picks up an {@link ItemEntity} from the ground after the {@link ItemStack} has been added to the player inventory.
         * <p>This events main purpose is to notify that the item pickup has happened.
         *
         * @param player     the player that touched <code>itemEntity</code>, picking up <code>stack</code> in the process
         * @param itemEntity the {@link ItemEntity} that was touched
         * @param stack      a stack copy containing the amount that was added to the player inventory, usually the full stack from <code>itemEntity</code>,
         *                   but can also be an empty stack in case the item pick-up was forced without considering the player inventory via {@link Touch}
         */
        void onItemPickup(Player player, ItemEntity itemEntity, ItemStack stack);
    }
}
