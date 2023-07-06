package fuzs.puzzleslib.init.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * a basic copy of {@link net.minecraft.world.level.block.entity.BlockEntityType.Builder} which cannot be used due to a package-private class type being required in the constructor
 * we need our own builder instead of passing parameters directly and then handling the builder part in mod loader specific code
 * as we cannot reference arguments directly (the blocks) on Forge,
 * we need to use a {@link java.util.function.Supplier} together with this builder instead
 *
 * @param factory the block entity factory, copied from vanilla's package-private interface
 * @param blocks the blocks permitted by this block entity
 * @param <T> type of block entity
 */
public record ModBlockEntityTypeBuilder<T extends BlockEntity>(ModBlockEntitySupplier<T> factory, Block... blocks) {

    /**
     * @param factory the block entity factory, copied from vanilla's package-private interface
     * @param blocks the blocks permitted by this block entity
     * @param <T> type of block entity
     * @return new builder instance
     */
    public static <T extends BlockEntity> ModBlockEntityTypeBuilder<T> of(ModBlockEntitySupplier<T> factory, Block... blocks) {
        return new ModBlockEntityTypeBuilder<>(factory, blocks);
    }

    /**
     * copy of vanilla's package-private block entity factory
     * @param <T> block entity type
     */
    @FunctionalInterface
    public interface ModBlockEntitySupplier<T extends BlockEntity> {

        /**
         * creates a new block entity instance
         * @param pos   position block entity is created at
         * @param state state block entity is created for
         * @return      the block entity
         */
        T create(BlockPos pos, BlockState state);
    }
}
