package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class BlockStateResolverContextFabricImpl implements BlockStateResolverContext {

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
                blockStateConsumer.accept(context::setModel);
            });
        });
    }

    @Override
    public <T> void registerBlockStateResolver(Block block, BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        PreparableModelLoadingPlugin.register(resourceLoader::apply, (T data, ModelLoadingPlugin.Context pluginContext) -> {
            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
                blockStateConsumer.accept(data, context::setModel);
            });
        });
    }
}
