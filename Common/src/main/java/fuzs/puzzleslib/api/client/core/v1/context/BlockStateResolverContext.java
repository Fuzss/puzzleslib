package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface BlockStateResolverContext {

    /**
     * Register a resolver responsible for mapping each {@link BlockState} of a block to an
     * {@link UnbakedBlockStateModel}.
     * <p>
     * Replaces the vanilla {@code JSON} files found in {@code assets/<namespace>/blockstates/}.
     *
     * @param block              the block to resolve block states for
     * @param blockStateConsumer the consumer resolving block states to individual unbaked block state models
     */
    void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer);
}
