package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Register a resolver responsible for mapping each {@link BlockState} of a block to an {@link UnbakedBlockStateModel}.
 * <p>
 * Replaces the vanilla {@code JSON} files found in {@code assets/<namespace>/blockstates/}.
 */
public interface BlockStateResolverContext {

    /**
     * @param block              the block to resolve block states for
     * @param blockStateConsumer the consumer resolving block states to individual unbaked block state models
     */
    void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer);

    /**
     * @param block              the block to resolve block states for
     * @param resourceLoader     the resource provider for asynchronously loaded data
     * @param blockStateConsumer the consumer resolving block states to individual unbaked block state models
     * @param <T>                the loaded data type
     */
    <T> void registerBlockStateResolver(Block block, BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer);
}
