package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemEntityEvents {
    public static final EventInvoker<Toss> TOSS = EventInvoker.lookup(Toss.class);
    public static final EventInvoker<Pickup> PICKUP = EventInvoker.lookup(Pickup.class);
    public static final EventInvoker<Touch> TOUCH = EventInvoker.lookup(Touch.class);

    private ItemEntityEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Touch {

        /**
         * Called when a player touches an {@link ItemEntity} laying on the ground.
         *
         * @param player     the player touching the item entity on the ground
         * @param itemEntity the item entity being touched
         * @return <ul>
         *         <li>{@link EventResult#ALLOW ALLOW} to force the item to be removed without there being any attempt at adding to the player inventory</li>
         *         <li>{@link EventResult#DENY DENY} to prevent the item from being picked up</li>
         *         <li>{@link EventResult#PASS PASS} to allow vanilla behavior to proceed where the item will be added to the player inventory, staying on the ground if that fails</li>
         *         </ul>
         */
        EventResult onItemTouch(Player player, ItemEntity itemEntity);
    }

    @FunctionalInterface
    public interface Pickup {

        /**
         * Called when the player picks up an {@link ItemEntity} from the ground after the {@link ItemStack} has been
         * added to the player inventory.
         * <p>
         * The event's purpose is to notify that the item pickup has happened.
         *
         * @param player     the player that touched the item entity, picking up the held item stack in the process
         * @param itemEntity the item entity that was touched
         * @param itemStack  an item stack copy containing the amount that was added to the player inventory, usually
         *                   the full item stack, but can also be an empty stack in case the item pick-up was forced
         *                   without considering the player inventory via {@link Touch}
         */
        void onItemPickup(Player player, ItemEntity itemEntity, ItemStack itemStack);
    }

    @FunctionalInterface
    public interface Toss {

        /**
         * Called when an item is tossed from the player inventory, either by pressing 'Q' or by clicking an item stack
         * outside a container screen.
         * <p>
         * This callback can be cancelled so no item entity is added to the level, the item will be lost in that case as
         * it has already been removed from the player inventory.
         * <p>
         * Use in combination with
         * {@link net.minecraft.world.entity.LivingEntity#createItemStackToDrop(ItemStack, boolean, boolean)} to create
         * a custom item entity.
         *
         * @param serverPlayer the player tossing the item stack
         * @param itemStack    the item stack being tossed
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the item from being tossed, nothing will be added to the level and the stack will be lost</li>
         *         <li>{@link EventResult#PASS PASS} to allow the item stack to be tossed from the player inventory</li>
         *         </ul>
         */
        EventResult onItemToss(ServerPlayer serverPlayer, ItemStack itemStack);
    }
}
