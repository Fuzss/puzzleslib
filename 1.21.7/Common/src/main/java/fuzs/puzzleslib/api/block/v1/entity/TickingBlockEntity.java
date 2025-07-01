package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.world.level.Level;

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
    default void clientTick() {
        // NO-OP
    }

    /**
     * Ticks the block entity on the server-side (meaning when {@link Level#isClientSide}) is <code>false</code>.
     * <p>
     * Used in vanilla e.g. for running cooking logic in
     * {@link net.minecraft.world.level.block.entity.FurnaceBlockEntity}.
     */
    default void serverTick() {
        // NO-OP
    }
}
