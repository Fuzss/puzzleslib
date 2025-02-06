package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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
    default <T> void registerBlockStateResolver(Block block, BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        this.registerBlockStateResolver(block, (ResourceLoaderContext context) -> {
            return resourceLoader.apply(context.resourceManager(), context.executor());
        }, blockStateConsumer);
    }

    /**
     * @param block              the block to resolve block states for
     * @param resourceLoader     the resource provider for asynchronously loaded data
     * @param blockStateConsumer the consumer resolving block states to individual unbaked block state models
     * @param <T>                the loaded data type
     */
    <T> void registerBlockStateResolver(Block block, Function<ResourceLoaderContext, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer);

    /**
     * A context for preparing loadable resources.
     */
    interface ResourceLoaderContext {

        /**
         * @return the resource manager
         */
        ResourceManager resourceManager();

        /**
         * @return the executor
         */
        Executor executor();

        /**
         * Allows for adding additional unbaked models. The models are automatically resolved and baked.
         *
         * @param resourceLocation the model resource location
         * @param unbakedModel     the unbaked model instance
         */
        void addModel(ResourceLocation resourceLocation, UnbakedModel unbakedModel);
    }
}
