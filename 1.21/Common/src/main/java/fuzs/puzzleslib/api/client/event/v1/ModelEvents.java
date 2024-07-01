package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModelEvents {
    public static final EventInvoker<ModifyUnbakedModel> MODIFY_UNBAKED_MODEL = EventInvoker.lookup(ModifyUnbakedModel.class);
    public static final EventInvoker<ModifyBakedModel> MODIFY_BAKED_MODEL = EventInvoker.lookup(ModifyBakedModel.class);
    public static final EventInvoker<AdditionalBakedModel> ADDITIONAL_BAKED_MODEL = EventInvoker.lookup(AdditionalBakedModel.class);
    public static final EventInvoker<AfterModelLoading> AFTER_MODEL_LOADING = EventInvoker.lookup(AfterModelLoading.class);

    private ModelEvents() {

    }

    @FunctionalInterface
    public interface ModifyUnbakedModel {

        /**
         * An event that runs for every unbaked model to allow for replacing the model.
         * <p>Only supports top level models (meaning mainly block state models and item inventory variants) on Forge.
         * <p>It is recommended for callers to cache returned models themselves, as the event runs for every single model location, even when the models would match per identity.
         *
         * @param modelLocation identifier for the unbaked model
         * @param unbakedModel  the unbaked model
         * @param modelGetter   get unbaked models from the model bakery
         * @param modelAdder    add additional unbaked models that are used in the returned model (like as part of {@link net.minecraft.client.renderer.block.model.multipart.Selector}s in {@link net.minecraft.client.renderer.block.model.multipart.MultiPart} models)
         * @return {@link EventResultHolder#interrupt(Object)} to replace the unbaked model,
         * {@link EventResultHolder#pass()} to let the original unbaked model go ahead
         */
        EventResultHolder<UnbakedModel> onModifyUnbakedModel(ResourceLocation modelLocation, Supplier<UnbakedModel> unbakedModel, Function<ResourceLocation, UnbakedModel> modelGetter, BiConsumer<ResourceLocation, UnbakedModel> modelAdder);
    }

    @FunctionalInterface
    public interface ModifyBakedModel {

        /**
         * An event that runs for every baked model to allow for replacing the model.
         * <p>Only supports top level models (meaning mainly block state models and item inventory variants) on Forge.
         * <p>It is recommended for callers to cache returned models themselves, as the event runs for every single model location, even when the models would match per identity.
         *
         * @param modelLocation identifier for the baked model
         * @param bakedModel    the baked model
         * @param modelBaker    the model baker used for baking the model, allows for retrieving and baking unbaked models
         * @param modelGetter   get baked models from the bakery, if only an unbaked model is present it will be baked automatically
         * @param modelAdder    add a baked model to the bakery, existing models cannot be replaced
         * @return {@link EventResultHolder#interrupt(Object)} to replace the baked model,
         * {@link EventResultHolder#pass()} to let the original baked model go ahead
         */
        EventResultHolder<BakedModel> onModifyBakedModel(ResourceLocation modelLocation, Supplier<BakedModel> bakedModel, Supplier<ModelBaker> modelBaker, Function<ResourceLocation, BakedModel> modelGetter, BiConsumer<ResourceLocation, BakedModel> modelAdder);
    }

    @FunctionalInterface
    public interface AdditionalBakedModel {

        /**
         * An event that allows for adding baked models that will be available via {@link ModelManager#getModel(ModelResourceLocation)} after baking has completed.
         * Mainly useful for storing custom baked models in the model manager.
         * <p>Cannot be used for replacing baked models in the bakery, use {@link ModifyBakedModel} for that.
         *
         * @param modelAdder  add a baked model to the bakery, existing models cannot be replaced
         * @param modelGetter get baked models from the bakery, if only an unbaked model is present it will be baked automatically
         * @param modelBaker  the model baker used for baking the model, allows for retrieving and baking unbaked models
         */
        void onAdditionalBakedModel(BiConsumer<ResourceLocation, BakedModel> modelAdder, Function<ResourceLocation, BakedModel> modelGetter, Supplier<ModelBaker> modelBaker);
    }

    @FunctionalInterface
    public interface AfterModelLoading {

        /**
         * Fired after the resource manager has reloaded models. Does not allow for modifying the loaded models map.
         * <p>Use a {@link Supplier} for {@link ModelManager} and {@link ModelBakery} to prevent an issue with loading the {@link net.minecraft.client.renderer.Sheets} too early on Fabric,
         * preventing modded materials from being added.
         *
         * @param modelManager model manager instance
         */
        void onAfterModelLoading(Supplier<ModelManager> modelManager);
    }
}