package fuzs.puzzleslib.impl.client.event;

import net.minecraft.Util;
import net.minecraft.client.resources.model.*;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.function.Function;

public final class ModelLoadingHelper {
    private static final Function<Class<?>, MethodHandle> MODEL_BAKERY_GETTERS = Util.memoize(
            ModelLoadingHelper::getModelBakery);
    @Nullable
    private static BlockStateModelLoader blockStateModelLoader;

    private ModelLoadingHelper() {
        // NO-OP
    }

    public static ModelBakery getModelBakery(ModelBaker modelBaker) {
        try {
            return (ModelBakery) MODEL_BAKERY_GETTERS.apply(modelBaker.getClass()).invoke(modelBaker);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private static MethodHandle getModelBakery(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() == ModelBakery.class) {
                field.setAccessible(true);
                try {
                    return MethodHandles.lookup().unreflectGetter(field);
                } catch (IllegalAccessException ignored) {
                    // NO-OP
                }
            }
        }

        throw new RuntimeException("Missing model bakery");
    }

    public static void setBlockStateModelLoader(BlockStateModelLoader blockStateModelLoader) {
        // do not discard this at the end of model loading,
        // with ModernFix models can be loaded at any time, and we never know when it is needed
        ModelLoadingHelper.blockStateModelLoader = blockStateModelLoader;
    }

    public static Function<ModelResourceLocation, UnbakedModel> getUnbakedTopLevelModel(ModelBakery modelBakery) {
        return (ModelResourceLocation modelResourceLocation) -> {
            return getUnbakedTopLevelModel(modelBakery, modelResourceLocation);
        };
    }

    private static UnbakedModel getUnbakedTopLevelModel(ModelBakery modelBakery, ModelResourceLocation modelResourceLocation) {
        UnbakedModel unbakedModel = modelBakery.topModels.get(modelResourceLocation);
        return unbakedModel != null ? unbakedModel : modelBakery.topModels.get(MissingBlockModel.VARIANT);
//        UnbakedModel unbakedModel = modelBakery.topModels.get(modelResourceLocation);
//        // when ModernFix is installed the model will likely be missing from ModelBakery#topLevelModels
//        // so try to load it manually here, alternatively ModelManager::getModel works as well in some scenarios
//        if (unbakedModel == null && blockStateModelLoader != null) {
//            unbakedModel = loadUnbakedBlockStateModel(modelBakery, blockStateModelLoader, modelResourceLocation);
//        } else if (unbakedModel != null) {
//            // this is necessary, or else some random block states will have missed out on it for some reason
//            unbakedModel.resolveDependencies(modelBakery::getModel);
//        }
//        if (unbakedModel == null) {
//            return modelBakery.topModels.get(MissingBlockModel.VARIANT);
//        } else {
//            return unbakedModel;
//        }
    }

//    @Nullable
//    public static UnbakedModel loadUnbakedBlockStateModel(ModelBakery modelBakery, BlockStateModelLoader blockStateModelLoader, ModelResourceLocation modelResourceLocation) {
//        return BuiltInRegistries.BLOCK.getOptional(modelResourceLocation.id()).map(block -> {
//            blockStateModelLoader.loadBlockStateDefinitionStack(modelResourceLocation.id(), block.getStateDefinition());
//            UnbakedModel unbakedModel = modelBakery.topModels.get(modelResourceLocation);
//            if (unbakedModel != null) unbakedModel.resolveDependencies(modelBakery::getModel);
//            return unbakedModel;
//        }).orElse(null);
//    }
}
