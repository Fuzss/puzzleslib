package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.impl.client.core.context.ResourceLoaderContextImpl;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public <T> void registerBlockStateResolver(Block block, Function<ResourceLoaderContext, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        Map<ResourceLocation, UnbakedModel> unbakedPlainModels = new HashMap<>();
        PreparableModelLoadingPlugin.register((ResourceManager resourceManager, Executor executor) -> {
            return resourceLoader.apply(new ResourceLoaderContextImpl(resourceManager, executor, unbakedPlainModels));
        }, (T data, ModelLoadingPlugin.Context pluginContext) -> {
            pluginContext.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
                blockStateConsumer.accept(data, context::setModel);
            });
            pluginContext.modifyModelOnLoad()
                    .register((@Nullable UnbakedModel model, ModelModifier.OnLoad.Context context) -> {
                        if (unbakedPlainModels.containsKey(context.id())) {
                            return unbakedPlainModels.remove(context.id());
                        } else {
                            return model;
                        }
                    });
        });
    }
}
