package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A utility interface for {@link net.minecraft.world.level.block.entity.BlockEntity}, allowing for the implementation
 * of non-static tick methods.
 * <p>
 * Inspired by <a
 * href="https://github.com/Shadows-of-Fire/Placebo/blob/1.20/src/main/java/dev/shadowsoffire/placebo/block_entity/TickingBlockEntity.java">TickingBlockEntity.java</a>.
 */
public interface TickingBlockEntity {

    /**
     * Ticks the block entity on the client-side.
     *
     * @param level      the level from {@link BlockEntity#getLevel()}
     * @param blockPos   the block position from {@link BlockEntity#getBlockPos()}
     * @param blockState the block state from {@link BlockEntity#getBlockState()}
     */
    default void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
        // NO-OP
    }

    /**
     * Ticks the block entity on the server-side.
     *
     * @param serverLevel the level from {@link BlockEntity#getLevel()}
     * @param blockPos    the block position from {@link BlockEntity#getBlockPos()}
     * @param blockState  the block state from {@link BlockEntity#getBlockState()}
     */
    default void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        // NO-OP
    }
}
