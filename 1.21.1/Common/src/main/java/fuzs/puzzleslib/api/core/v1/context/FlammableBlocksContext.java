package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.level.block.Block;

/**
 * Register blocks that {@link net.minecraft.world.level.block.FireBlock} can spread to.
 */
@Deprecated
@FunctionalInterface
public interface FlammableBlocksContext {

    /**
     * Register blocks that {@link net.minecraft.world.level.block.FireBlock} can spread to.
     *
     * @param encouragement a value determining how fast this block will spread fire to other nearby flammable blocks
     * @param flammability  a value determining how easily this block catches on fire from nearby fires
     * @param blocks        blocks to register <code>encouragement</code> and <code>flammability</code> for
     */
    void registerFlammable(int encouragement, int flammability, Block... blocks);
}
