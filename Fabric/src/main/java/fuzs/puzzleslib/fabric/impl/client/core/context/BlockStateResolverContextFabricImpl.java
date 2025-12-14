package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * TODO enable implementation again when available in Fabric Api
 */
public final class BlockStateResolverContextFabricImpl implements BlockStateResolverContext {

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
//        ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
//            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
//                blockStateConsumer.accept(context::setModel);
//            });
//        });
    }

    @Override
    public <T> void registerBlockStateResolver(Block block, BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
//        PreparableModelLoadingPlugin.register((PreparableReloadListener.SharedState sharedState, Executor backgroundExecutor) -> resourceLoader.apply(
//                sharedState.resourceManager(),
//                backgroundExecutor), (T data, ModelLoadingPlugin.Context pluginContext) -> {
//            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
//                blockStateConsumer.accept(data, context::setModel);
//            });
//        });
    }
}
