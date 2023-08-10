package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class PlayerEvents {
    public static final EventInvoker<BreakSpeed> BREAK_SPEED = EventInvoker.lookup(BreakSpeed.class);
    public static final EventInvoker<Copy> COPY = EventInvoker.lookup(Copy.class);
    public static final EventInvoker<Respawn> RESPAWN = EventInvoker.lookup(Respawn.class);
    public static final EventInvoker<StartTracking> START_TRACKING = EventInvoker.lookup(StartTracking.class);
    public static final EventInvoker<StopTracking> STOP_TRACKING = EventInvoker.lookup(StopTracking.class);
    public static final EventInvoker<LoggedIn> LOGGED_IN = EventInvoker.lookup(LoggedIn.class);
    public static final EventInvoker<LoggedOut> LOGGED_OUT = EventInvoker.lookup(LoggedOut.class);
    public static final EventInvoker<AfterChangeDimension> AFTER_CHANGE_DIMENSION = EventInvoker.lookup(AfterChangeDimension.class);
    public static final EventInvoker<ItemPickup> ITEM_PICKUP = EventInvoker.lookup(ItemPickup.class);

    private PlayerEvents() {

    }

    @FunctionalInterface
    public interface BreakSpeed {

        /**
         * Called when the player attempts to harvest a block in {@link Player#getDestroySpeed(BlockState)}.
         *
         * @param player     the player breaking <code>state</code>
         * @param state      the block state being broken
         * @param breakSpeed the speed at which the block is broken, usually a value around 1.0
         * @return {@link EventResult#INTERRUPT} to prevent the block from breaking, effectively setting the break speed to -1.0,
         * {@link EventResult#PASS} to allow the block to be broken at the defined break speed
         */
        EventResult onBreakSpeed(Player player, BlockState state, DefaultedFloat breakSpeed);
    }

    @FunctionalInterface
    public interface Copy {

        /**
         * Called when player data is copied to a new player.
         *
         * @param oldPlayer the old player
         * @param newPlayer the new player
         * @param alive     whether the copy was made when returning from the End dimension, otherwise caused by the old player having died
         */
        void onCopy(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive);
    }

    @FunctionalInterface
    public interface Respawn {

        /**
         * Called after player a has been respawned.
         *
         * @param player the player
         * @param alive  whether the copy was made when returning from the End dimension, otherwise caused by the old player having died
         */
        void onRespawn(ServerPlayer player, boolean alive);
    }

    @FunctionalInterface
    public interface StartTracking {

        /**
         * Called before an entity starts getting tracked by a player, meaning the player receives updates about the entity like its motion.
         *
         * @param trackedEntity the entity that will be tracked
         * @param player        the player that will track the entity
         */
        void onStartTracking(Entity trackedEntity, ServerPlayer player);
    }

    @FunctionalInterface
    public interface StopTracking {

        /**
         * Called after an entity stops getting tracked by a player.
         *
         * @param trackedEntity the entity that is no longer being tracked
         * @param player        the player that is no longer tracking the entity
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
         * @param from   the original world the player was in
         * @param to     the new world the player was moved to
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
