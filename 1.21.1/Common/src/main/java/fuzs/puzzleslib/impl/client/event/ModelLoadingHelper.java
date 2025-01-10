package fuzs.puzzleslib.impl.client.event;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public final class ModelLoadingHelper {
    private static WeakReference<@Nullable BlockStateModelLoader> modelLoaderReference = new WeakReference<>(null);

    private ModelLoadingHelper() {
        // NO-OP
    }

    public static void setModelLoader(BlockStateModelLoader modelLoader) {
        // do not discard this at the end of model loading,
        // with ModernFix models can be loaded at any time, and we never know when it is needed
        // wrapping in a weak reference should be fine though, so it is only held on to when necessary (and still strongly referenced by ModernFix)
        ModelLoadingHelper.modelLoaderReference = new WeakReference<>(modelLoader);
    }

    public static Function<ModelResourceLocation, UnbakedModel> getUnbakedTopLevelModel(ModelBakery modelBakery) {
        return (ModelResourceLocation modelResourceLocation) -> {
            return getUnbakedTopLevelModel(modelBakery, modelResourceLocation);
        };
    }

    private static UnbakedModel getUnbakedTopLevelModel(ModelBakery modelBakery, ModelResourceLocation modelResourceLocation) {
        UnbakedModel unbakedModel = modelBakery.topLevelModels.get(modelResourceLocation);
        // when ModernFix is installed the model will likely be missing from ModelBakery#topLevelModels
        // so try to load it manually here, alternatively ModelManager::getModel works as well in some scenarios
        BlockStateModelLoader modelLoader = modelLoaderReference.get();
        if (unbakedModel == null && modelLoader != null) {
            unbakedModel = loadUnbakedBlockStateModel(modelBakery, modelLoader, modelResourceLocation);
        } else if (unbakedModel != null) {
            // this is necessary, or else some random block states will have missed out on it for some reason
            unbakedModel.resolveParents(modelBakery::getModel);
        }
        if (unbakedModel == null) {
            return modelBakery.topLevelModels.get(ModelBakery.MISSING_MODEL_VARIANT);
        } else {
            return unbakedModel;
        }
    }

    @Nullable
    public static UnbakedModel loadUnbakedBlockStateModel(ModelBakery modelBakery, BlockStateModelLoader blockStateModelLoader, ModelResourceLocation modelResourceLocation) {
        return BuiltInRegistries.BLOCK.getOptional(modelResourceLocation.id()).map(block -> {
            blockStateModelLoader.loadBlockStateDefinitions(modelResourceLocation.id(), block.getStateDefinition());
            UnbakedModel unbakedModel = modelBakery.topLevelModels.get(modelResourceLocation);
            if (unbakedModel != null) unbakedModel.resolveParents(modelBakery::getModel);
            return unbakedModel;
        }).orElse(null);
    }
}
