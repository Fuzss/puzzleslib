package fuzs.puzzleslib.api.client.util.v1;

import com.google.gson.JsonObject;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ModelLoadingHelper {
    public static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
    public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");

    private ModelLoadingHelper() {
        // NO-OP
    }

    @Deprecated
    public static BlockStateModelLoader.LoadedModels loadBlockState(ResourceManager resourceManager, Block block) {
        return loadBlockState(resourceManager, block, Util.backgroundExecutor()).join();
    }

    public static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockState(ResourceManager resourceManager, Block block, Executor executor) {
        return loadBlockState(resourceManager,
                BuiltInRegistries.BLOCK.getKey(block),
                block.getStateDefinition(),
                executor);
    }

    @Deprecated
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

    public static CompletableFuture<List<BlockStateModelLoader.LoadedBlockModelDefinition>> loadBlockState(ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.getResourceStack(ModelLoadingHelper.BLOCKSTATE_LISTER.idToFile(
                resourceLocation)), executor).thenApply((List<Resource> resourceStack) -> {
            List<BlockStateModelLoader.LoadedBlockModelDefinition> blockModelDefinitions = new ArrayList<>(resourceStack.size());

            for (Resource resource : resourceStack) {
                try (Reader reader = resource.openAsReader()) {
                    JsonObject jsonObject = GsonHelper.parse(reader);
                    BlockModelDefinition blockModelDefinition = BlockModelDefinition.fromJsonElement(jsonObject);
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

    public static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockState(List<BlockStateModelLoader.LoadedBlockModelDefinition> loadedBlockModelDefinitions, ResourceLocation resourceLocation, StateDefinition<Block, BlockState> stateDefinition, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return BlockStateModelLoader.loadBlockStateDefinitionStack(resourceLocation,
                        stateDefinition,
                        loadedBlockModelDefinitions,
                        null);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.error("Failed to load blockstate definition {}", resourceLocation, exception);
                return null;
            }
        }, executor);
    }

    @Deprecated
    public static UnbakedModel loadBlockModel(ResourceManager resourceManager, ResourceLocation resourceLocation, UnbakedModel missingModel) {
        return loadBlockModel(resourceManager, resourceLocation, Util.backgroundExecutor(), missingModel).join();
    }

    public static CompletableFuture<UnbakedModel> loadBlockModel(ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor, UnbakedModel missingModel) {
        return CompletableFuture.supplyAsync(() -> resourceManager.getResource(MODEL_LISTER.idToFile(resourceLocation)),
                executor).thenApply((Optional<Resource> optional) -> {
            return optional.<UnbakedModel>map((Resource resource) -> {
                try (Reader reader = resource.openAsReader()) {
                    return BlockModel.fromStream(reader);
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to load model {}", resourceLocation, exception);
                    return null;
                }
            }).orElse(missingModel);
        });
    }

    public static UnbakedBlockStateModel missingModel() {
        return new UnbakedBlockStateModel() {

            @Override
            public BakedModel bake(ModelBaker baker) {
                return UnbakedModel.bakeWithTopModelValues(MissingBlockModel.missingModel(),
                        baker,
                        BlockModelRotation.X0_Y0);
            }

            @Override
            public Object visualEqualityGroup(BlockState state) {
                return this;
            }

            @Override
            public void resolveDependencies(Resolver resolver) {
                // NO-OP
            }
        };
    }
}
