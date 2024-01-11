package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockEvents {
    public static final EventInvoker<Break> BREAK = EventInvoker.lookup(Break.class);
    public static final EventInvoker<DropExperience> DROP_EXPERIENCE = EventInvoker.lookup(DropExperience.class);
    public static final EventInvoker<FarmlandTrample> FARMLAND_TRAMPLE = EventInvoker.lookup(FarmlandTrample.class);

    private BlockEvents() {

    }

    @FunctionalInterface
    public interface Break {

        /**
         * Fires before a block is about to be broken, allows for preventing just that.
         *
         * @param level      the level the block is being broken in
         * @param pos        the position of the block
         * @param state      the block state before it is broken
         * @param player     the player involved with breaking the block
         * @param itemInHand the tool used to mine the block held in the player's main hand
         * @return {@link EventResult#INTERRUPT} to prevent the block from being broken, it will remain in the level and a packet notifying the client will be sent,
         * {@link EventResult#PASS} to allow the block to be broken
         */
        EventResult onBreakBlock(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack itemInHand);
    }

    @FunctionalInterface
    public interface DropExperience {

        /**
         * Fires when a block is about to drop experience after being broken.
         * <p>While in vanilla only a few blocks will drop experience (mainly ore blocks),
         * this callback runs for every block, allowing for potentially adding an experience drop.
         *
         * @param level            the level the block is being broken in
         * @param pos              the position of the block
         * @param state            the block state before it is broken
         * @param player           the player involved with breaking the block
         * @param itemInHand       the tool used to mine the block held in the player's main hand
         * @param experienceToDrop the amount of experience to drop for this block
         */
        void onDropExperience(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack itemInHand, MutableInt experienceToDrop);
    }

    @FunctionalInterface
    public interface FarmlandTrample {

        /**
         * Fired when an entity falls onto a block of farmland and in the process would trample on it, turning the block into dirt and destroying potential crops.
         *
         * @param level        level farmland block is trampled in
         * @param pos          farmland block position
         * @param state        block state farmland will be converted to after trampling
         * @param fallDistance fall distance of the entity
         * @param entity       the entity falling on the farmland block
         * @return {@link EventResult#INTERRUPT} to prevent trampling,
         * {@link EventResult#PASS} to allow the trampling
         */
        EventResult onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity);
    }
}
