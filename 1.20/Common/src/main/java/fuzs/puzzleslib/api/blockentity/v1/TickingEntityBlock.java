package fuzs.puzzleslib.api.blockentity.v1;

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
 */
public interface TickingEntityBlock extends EntityBlock {

    /**
     * Get the {@link BlockEntityType} for this block.
     *
     * @param <T> type of the corresponding {@link BlockEntity}
     * @return tne block entity type token
     */
    <T extends BlockEntity & TickingBlockEntity> BlockEntityType<T> getBlockEntityType();

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        // due to the type bounds in TickingEntityBlock::getBlockEntityType this guarantees we have a TickingBlockEntity instance
        if (blockEntityType == this.getBlockEntityType()) {
            Consumer<TickingBlockEntity> ticker = level.isClientSide ? TickingBlockEntity::clientTick : TickingBlockEntity::serverTick;
            return (Level $, BlockPos blockPos, BlockState blockState, T blockEntity) -> {
                // no need to pass on anything, the block entity instance already has all those parameters
                ticker.accept((TickingBlockEntity) blockEntity);
            };
        }
        return null;
    }
}
