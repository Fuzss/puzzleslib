package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockEvents {
    public static final EventInvoker<Break> BREAK = EventInvoker.lookup(Break.class);
    public static final EventInvoker<DropExperience> DROP_EXPERIENCE = EventInvoker.lookup(DropExperience.class);

    private BlockEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Break {

        /**
         * Fires before a block is about to be broken, allows for preventing just that.
         *
         * @param serverLevel  the level the block is being broken in
         * @param blockPos     the position of the block
         * @param blockState   the block state before it is broken
         * @param serverPlayer the player involved with breaking the block
         * @param itemInHand   the tool used to mine the block held in the player's main hand
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the block from being broken, it will remain in the level and a packet notifying the client will be sent</li>
         *         <li>{@link EventResult#PASS PASS} to allow the block to be broken</li>
         *         </ul>
         */
        EventResult onBreakBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, ServerPlayer serverPlayer, ItemStack itemInHand);
    }

    @FunctionalInterface
    public interface DropExperience {

        /**
         * Fires when a block is about to drop experience after being broken.
         * <p>
         * While in vanilla only a few blocks will drop experience (mainly ore blocks), this callback runs for every
         * block, allowing for potentially adding an experience drop.
         *
         * @param serverLevel      the level the block is being broken in
         * @param blockPos         the position of the block
         * @param blockState       the block state before it is broken
         * @param serverPlayer     the player involved with breaking the block
         * @param itemInHand       the tool used to mine the block held in the player's main hand
         * @param experienceAmount the amount of experience to drop for this block
         */
        void onDropExperience(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, ServerPlayer serverPlayer, ItemStack itemInHand, MutableInt experienceAmount);
    }
}
