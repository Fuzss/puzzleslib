package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ModelLoadingHelper {

    private ModelLoadingHelper() {
        // NO-OP
    }

    @ApiStatus.Experimental
    public static BlockStateModelLoader.LoadedModels loadBlockState(ResourceManager resourceManager, Block block) {
        return loadBlockState(resourceManager, block, Util.backgroundExecutor()).join();
    }

    public static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockState(ResourceManager resourceManager, Block block, Executor executor) {
        return loadBlockState(resourceManager,
                BuiltInRegistries.BLOCK.getKey(block),
                block.getStateDefinition(),
                executor);
    }

    @ApiStatus.Experimental
    public static BlockStateModelLoader.LoadedModels loadBlockState(ResourceManager resourceManager, ResourceLocation resourceLocation, StateDefinition<Block, BlockState> stateDefinition) {
        return loadBlockState(resourceManager, resourceLocation, stateDefinition, Util.backgroundExecutor()).join();
    }

    public static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockState(ResourceManager resourceManager, ResourceLocation resourceLocation, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return loadBlockState(resourceManager, resourceLocation, resourceLocation, stateDefinition, executor);
    }

    public static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockState(ResourceManager resourceManager, ResourceLocation oldResourceLocation, ResourceLocation newResourceLocation, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return loadBlockState(resourceManager,
                oldResourceLocation,
                executor).thenCompose((List<BlockStateModelLoader.LoadedBlockModelDefinition> loadedBlockModelDefinitions) -> {
            return loadBlockState(loadedBlockModelDefinitions, newResourceLocation, stateDefinition, executor);
        });
    }

    /**
     * Similar to {@link BlockStateModelLoader#loadBlockStates(ResourceManager, Executor)}.
     */
    public static CompletableFuture<List<BlockStateModelLoader.LoadedBlockModelDefinition>> loadBlockState(ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.getResourceStack(BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(
                resourceLocation)), executor).thenApply((List<Resource> resourceStack) -> {
            List<BlockStateModelLoader.LoadedBlockModelDefinition> blockModelDefinitions = new ArrayList<>(resourceStack.size());

            for (Resource resource : resourceStack) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    BlockModelDefinition blockModelDefinition = BlockModelDefinition.CODEC.parse(JsonOps.INSTANCE,
                            jsonElement).getOrThrow(JsonParseException::new);
                    blockModelDefinitions.add(new BlockStateModelLoader.LoadedBlockModelDefinition(resource.sourcePackId(),
                            blockModelDefinition));
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to load blockstate definition {} from pack {}",
                            resourceLocation,
                            resource.sourcePackId(),
                            exception);
                }
            }

            return blockModelDefinitions;
        });
    }

    /**
     * Similar to {@link BlockStateModelLoader#loadBlockStates(ResourceManager, Executor)}.
     */
    public static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockState(List<BlockStateModelLoader.LoadedBlockModelDefinition> loadedBlockModelDefinitions, ResourceLocation resourceLocation, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return BlockStateModelLoader.loadBlockStateDefinitionStack(resourceLocation,
                        stateDefinition,
                        loadedBlockModelDefinitions);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.error("Failed to load blockstate definition {}", resourceLocation, exception);
                return null;
            }
        }, executor);
    }

    @ApiStatus.Experimental
    public static @Nullable UnbakedModel loadBlockModel(ResourceManager resourceManager, ResourceLocation resourceLocation) {
        return loadBlockModel(resourceManager, resourceLocation, Util.backgroundExecutor()).join();
    }

    /**
     * Similar to {@link ModelManager#loadBlockModels(ResourceManager, Executor)}.
     */
    public static CompletableFuture<@Nullable UnbakedModel> loadBlockModel(ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.getResource(ModelManager.MODEL_LISTER.idToFile(
                resourceLocation)), executor).thenApply((Optional<Resource> optional) -> {
            return optional.<UnbakedModel>map((Resource resource) -> {
                try (Reader reader = resource.openAsReader()) {
                    return BlockModel.fromStream(reader);
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to load model {}", resourceLocation, exception);
                    return null;
                }
            }).orElse(null);
        });
    }

    public static BlockStateModel.UnbakedRoot missingModel() {
        return new BlockStateModel.UnbakedRoot() {
            @Override
            public BlockStateModel bake(BlockState blockState, ModelBaker modelBaker) {
                UnbakedModel unbakedModel = MissingBlockModel.missingModel();
                // just use this, so we do not have to deal with the internal resolved model implementation
                ResolvedModel resolvedModel = new ModelDiscovery(Collections.emptyMap(), unbakedModel).missingModel();
                return ModelBakery.MissingModels.bake(resolvedModel, modelBaker.sprites()).block();
            }

            @Override
            public Object visualEqualityGroup(BlockState state) {
                return this;
            }

            @Override
            public void resolveDependencies(ResolvableModel.Resolver resolver) {
                // NO-OP
            }
        };
    }
}
