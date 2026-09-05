package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.google.gson.JsonObject;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ModelLoadingHelper {

    private ModelLoadingHelper() {
        // NO-OP
    }

    @ApiStatus.Experimental
    public static Map<BlockState, UnbakedModel> loadBlockState(ResourceManager resourceManager, Block block) {
        return loadBlockState(resourceManager, block, Util.backgroundExecutor()).join();
    }

    public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(ResourceManager resourceManager, Block block, Executor executor) {
        return loadBlockState(resourceManager,
                BuiltInRegistries.BLOCK.getKey(block),
                block.getStateDefinition(),
                executor);
    }

    @ApiStatus.Experimental
    public static Map<BlockState, UnbakedModel> loadBlockState(ResourceManager resourceManager, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition) {
        return loadBlockState(resourceManager, blockId, stateDefinition, Util.backgroundExecutor()).join();
    }

    public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(ResourceManager resourceManager, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return loadBlockState(resourceManager, blockId, blockId, stateDefinition, executor);
    }

    public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(ResourceManager resourceManager, ResourceLocation sourceBlockId, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return loadBlockState(resourceManager,
                sourceBlockId,
                executor).thenCompose((List<BlockStateModelLoader.LoadedJson> loadedBlockModelDefinitions) -> {
            return loadBlockState(loadedBlockModelDefinitions, blockId, stateDefinition, executor);
        });
    }

    /**
     * @see ModelManager#loadBlockStates(ResourceManager, Executor)
     */
    public static CompletableFuture<List<BlockStateModelLoader.LoadedJson>> loadBlockState(ResourceManager resourceManager, ResourceLocation blockId, Executor executor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.getResourceStack(BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(
                blockId)), executor).thenApply((List<Resource> resourceStack) -> {
            List<BlockStateModelLoader.LoadedJson> blockModelDefinitions = new ArrayList<>(resourceStack.size());

            for (Resource resource : resourceStack) {
                try (Reader reader = resource.openAsReader()) {
                    JsonObject jsonObject = GsonHelper.parse(reader);
                    blockModelDefinitions.add(new BlockStateModelLoader.LoadedJson(resource.sourcePackId(),
                            jsonObject));
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to load blockstate definition {} from pack {}",
                            blockId,
                            resource.sourcePackId(),
                            exception);
                }
            }

            return blockModelDefinitions;
        });
    }

    /**
     * @see BlockStateModelLoader#loadBlockStateDefinitions(ResourceLocation, StateDefinition)
     */
    public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(List<BlockStateModelLoader.LoadedJson> loadedBlockModelDefinitions, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources = Collections.singletonMap(
                        BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(blockId),
                        loadedBlockModelDefinitions);
                Map<BlockState, UnbakedModel> discoveredModelOutput = new HashMap<>();
                Map<ModelResourceLocation, BlockState> blockStateIds = new HashMap<>();
                for (BlockState blockState : stateDefinition.getPossibleStates()) {
                    blockStateIds.put(BlockModelShaper.stateToModelLocation(blockId, blockState), blockState);
                }

                new BlockStateModelLoader(blockStateResources,
                        InactiveProfiler.INSTANCE,
                        missingModel(),
                        Minecraft.getInstance().getBlockColors(),
                        (ModelResourceLocation modelId, UnbakedModel model) -> {
                            BlockState blockState = blockStateIds.get(modelId);
                            Objects.requireNonNull(blockState, "block state is null");
                            discoveredModelOutput.put(blockState, model);
                        }).loadBlockStateDefinitions(blockId, stateDefinition);
                return discoveredModelOutput;
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.error("Failed to load blockstate definition {}", blockId, exception);
                return null;
            }
        }, executor);
    }

    @ApiStatus.Experimental
    public static @Nullable BlockModel loadBlockModel(ResourceManager resourceManager, ResourceLocation modelId) {
        return loadBlockModel(resourceManager, modelId, Util.backgroundExecutor()).join();
    }

    /**
     * @see ModelManager#loadBlockModels(ResourceManager, Executor)
     */
    public static CompletableFuture<@Nullable BlockModel> loadBlockModel(ResourceManager resourceManager, ResourceLocation modelId, Executor executor) {
        // The model id is already processed by the call site, so it must not be passed through ModelBakery.MODEL_LISTER#idToFile again.
        return CompletableFuture.supplyAsync(() -> resourceManager.getResource(modelId), executor)
                .thenApply((Optional<Resource> optional) -> {
                    return optional.<BlockModel>map((Resource resource) -> {
                        try (Reader reader = resource.openAsReader()) {
                            return BlockModel.fromStream(reader);
                        } catch (Exception exception) {
                            PuzzlesLib.LOGGER.error("Failed to load model {}", modelId, exception);
                            return null;
                        }
                    }).orElse(null);
                });
    }

    /**
     * @see ModelBakery#loadBlockModel(ResourceLocation)
     */
    public static UnbakedModel missingModel() {
        BlockModel model = BlockModel.fromString(ModelBakery.MISSING_MODEL_MESH);
        model.name = ModelBakery.MISSING_MODEL_LOCATION.toString();
        return model;
    }
}
