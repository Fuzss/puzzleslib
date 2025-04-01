package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.impl.client.core.context.ResourceLoaderContextImpl;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BlockStateResolverContextFabricImpl implements BlockStateResolverContext {

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
                blockStateConsumer.accept(context::setModel);
            });
        });
    }

    @Override
    public <T> void registerBlockStateResolver(Block block, Function<ResourceLoaderContext, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        Map<ResourceLocation, ResolvedModel> resolvedModels = new HashMap<>();
        PreparableModelLoadingPlugin.register((ResourceManager resourceManager, Executor executor) -> {
            return resourceLoader.apply(new ResourceLoaderContextImpl(resourceManager, executor, resolvedModels));
        }, (T data, ModelLoadingPlugin.Context pluginContext) -> {
            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
                blockStateConsumer.accept(data, context::setModel);
            });
            pluginContext.modifyModelOnLoad().register((UnbakedModel model, ModelModifier.OnLoad.Context context) -> {
                if (resolvedModels.containsKey(context.id())) {
                    return resolvedModels.remove(context.id()).wrapped();
                } else {
                    return model;
                }
            });
        });
    }
}
