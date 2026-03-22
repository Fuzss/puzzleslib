package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * A simple extension to {@link EntityBlock} for a default implementation of
 * {@link EntityBlock#getTicker(Level, BlockState, BlockEntityType)}.
 *
 * @param <T> type of the corresponding {@link BlockEntity}
 */
public interface TickingEntityBlock<T extends BlockEntity & TickingBlockEntity> extends EntityBlock {

    /**
     * Get the {@link BlockEntityType} for this block.
     *
     * @return tne block entity type token
     */
    BlockEntityType<? extends T> getBlockEntityType();

    @Override
    default BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return this.getBlockEntityType().create(blockPos, blockState);
    }

    @Nullable
    @Override
    default <BE extends BlockEntity> BlockEntityTicker<BE> getTicker(Level level, BlockState blockState, BlockEntityType<BE> blockEntityType) {
        // due to the type bounds in TickingEntityBlock this guarantees we have a TickingBlockEntity instance
        return this.getBlockEntityType() == blockEntityType ? this.getBlockEntityTicker() : null;

    }

    private <BE extends BlockEntity> BlockEntityTicker<BE> getBlockEntityTicker() {
        return (Level level, BlockPos blockPos, BlockState blockState, BE blockEntity) -> {
            if (level instanceof ServerLevel serverLevel) {
                ((TickingBlockEntity) blockEntity).serverTick(serverLevel, blockPos, blockState);
            } else {
                ((TickingBlockEntity) blockEntity).clientTick(level, blockPos, blockState);
            }
        };
    }
}
