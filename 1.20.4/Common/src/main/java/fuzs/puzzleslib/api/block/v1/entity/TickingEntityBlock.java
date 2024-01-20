package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A simple extension to {@link EntityBlock} for a default implementation of {@link EntityBlock#getTicker(Level, BlockState, BlockEntityType)}.
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
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.getBlockEntityType().create(pos, state);
    }

    @Nullable
    @Override
    default <BE extends BlockEntity> BlockEntityTicker<BE> getTicker(Level level, BlockState state, BlockEntityType<BE> blockEntityType) {
        // due to the type bounds in TickingEntityBlock this guarantees we have a TickingBlockEntity instance
        if (this.getBlockEntityType().equals(blockEntityType)) {
            Consumer<TickingBlockEntity> ticker = level.isClientSide ? TickingBlockEntity::clientTick : TickingBlockEntity::serverTick;
            return (Level $, BlockPos blockPos, BlockState blockState, BE blockEntity) -> {
                // no need to pass on anything, the block entity instance already has all those parameters
                ticker.accept((TickingBlockEntity) blockEntity);
            };
        }
        return null;
    }
}
