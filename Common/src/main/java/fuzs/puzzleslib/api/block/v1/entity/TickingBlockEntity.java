package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A utility interface for extending {@link net.minecraft.world.level.block.entity.BlockEntity}, allowing to implement
 * non-static tick methods.
 * <p>
 * Inspired by <a
 * href="https://github.com/Shadows-of-Fire/Placebo/blob/1.20/src/main/java/dev/shadowsoffire/placebo/block_entity/TickingBlockEntity.java">TickingBlockEntity.java</a>.
 */
public interface TickingBlockEntity {

    /**
     * Ticks the block entity on the client-side (meaning when {@link Level#isClientSide}) is <code>true</code>.
     * <p>
     * Used in vanilla e.g. for animating the book in
     * {@link net.minecraft.world.level.block.entity.EnchantingTableBlockEntity}.
     */
    default void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
        this.clientTick();
    }

    /**
     * Ticks the block entity on the client-side (meaning when {@link Level#isClientSide}) is <code>true</code>.
     * <p>
     * Used in vanilla e.g. for animating the book in
     * {@link net.minecraft.world.level.block.entity.EnchantingTableBlockEntity}.
     */
    @Deprecated(forRemoval = true)
    default void clientTick() {
        // NO-OP
    }

    /**
     * Ticks the block entity on the server-side (meaning when {@link Level#isClientSide}) is <code>false</code>.
     * <p>
     * Used in vanilla e.g. for running cooking logic in
     * {@link net.minecraft.world.level.block.entity.FurnaceBlockEntity}.
     */
    default void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        this.serverTick();
    }

    /**
     * Ticks the block entity on the server-side (meaning when {@link Level#isClientSide}) is <code>false</code>.
     * <p>
     * Used in vanilla e.g. for running cooking logic in
     * {@link net.minecraft.world.level.block.entity.FurnaceBlockEntity}.
     */
    @Deprecated(forRemoval = true)
    default void serverTick() {
        // NO-OP
    }
}
