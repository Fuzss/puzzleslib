package fuzs.puzzleslib.api.client.util.v1;

import com.google.gson.JsonObject;
import fuzs.puzzleslib.impl.PuzzlesLib;
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
import java.util.Objects;

public final class ModelLoadingHelper {
    public static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
    public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");

    private ModelLoadingHelper() {
        // NO-OP
    }

    public static BlockStateModelLoader.LoadedModels loadBlockState(ResourceManager resourceManager, Block block) {
        return loadBlockState(resourceManager, BuiltInRegistries.BLOCK.getKey(block), block.getStateDefinition());
    }

    public static BlockStateModelLoader.LoadedModels loadBlockState(ResourceManager resourceManager, ResourceLocation resourceLocation, StateDefinition<Block, BlockState> stateDefinition) {
        Objects.requireNonNull(resourceManager, "resource location is null");
        Objects.requireNonNull(stateDefinition, "state definition is null");
        List<Resource> resourceStack = resourceManager.getResourceStack(BLOCKSTATE_LISTER.idToFile(resourceLocation));
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

        try {
            return BlockStateModelLoader.loadBlockStateDefinitionStack(resourceLocation,
                    stateDefinition,
                    blockModelDefinitions,
                    MissingBlockModel.missingModel());
        } catch (Exception exception) {
            PuzzlesLib.LOGGER.error("Failed to load blockstate definition {}", resourceLocation, exception);
            return null;
        }
    }

    public static UnbakedModel loadBlockModel(ResourceManager resourceManager, ResourceLocation resourceLocation) {
        return resourceManager.getResource(MODEL_LISTER.idToFile(resourceLocation))
                .<UnbakedModel>map((Resource resource) -> {
                    try (Reader reader = resource.openAsReader()) {
                        return BlockModel.fromStream(reader);
                    } catch (Exception exception) {
                        PuzzlesLib.LOGGER.error("Failed to load model {}", resourceLocation, exception);
                        return null;
                    }
                })
                .orElse(MissingBlockModel.missingModel());
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
