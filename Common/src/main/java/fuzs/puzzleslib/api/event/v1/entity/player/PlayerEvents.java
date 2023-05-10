package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PlayerEvents {
    public static final EventInvoker<StartTracking> START_TRACKING = EventInvoker.lookup(StartTracking.class);
    public static final EventInvoker<StopTracking> STOP_TRACKING = EventInvoker.lookup(StopTracking.class);
    public static final EventInvoker<LoggedIn> LOGGED_IN = EventInvoker.lookup(LoggedIn.class);
    public static final EventInvoker<LoggedOut> LOGGED_OUT = EventInvoker.lookup(LoggedOut.class);
    public static final EventInvoker<AfterChangeDimension> AFTER_CHANGE_DIMENSION = EventInvoker.lookup(AfterChangeDimension.class);
    public static final EventInvoker<ItemPickup> ITEM_PICKUP = EventInvoker.lookup(ItemPickup.class);

    private PlayerEvents() {

    }

    @FunctionalInterface
    public interface StartTracking {

        /**
         * Called before an entity starts getting tracked by a player, meaning the player receives updates about the entity like its motion.
         *
         * @param trackedEntity the entity that will be tracked
         * @param player the player that will track the entity
         */
        void onStartTracking(Entity trackedEntity, ServerPlayer player);
    }

    @FunctionalInterface
    public interface StopTracking {

        /**
         * Called after an entity stops getting tracked by a player.
         *
         * @param trackedEntity the entity that is no longer being tracked
         * @param player the player that is no longer tracking the entity
         */
        void onStopTracking(Entity trackedEntity, ServerPlayer player);
    }

    @FunctionalInterface
    public interface LoggedIn {

        /**
         * Called when a player joins the server and is added to the {@link net.minecraft.server.players.PlayerList}.
         *
         * @param player the player logging in
         */
        void onLoggedIn(ServerPlayer player);
    }

    @FunctionalInterface
    public interface LoggedOut {

        /**
         * Called when a player disconnects from the server and is removed from the {@link net.minecraft.server.players.PlayerList}.
         *
         * @param player the player logging out
         */
        void onLoggedOut(ServerPlayer player);
    }

    @FunctionalInterface
    public interface AfterChangeDimension {

        /**
         * Called after a player has been moved to different world.
         *
         * @param player the player
         * @param from the original world the player was in
         * @param to the new world the player was moved to
         */
        void onAfterChangeDimension(ServerPlayer player, ServerLevel from, ServerLevel to);
    }

    @FunctionalInterface
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
