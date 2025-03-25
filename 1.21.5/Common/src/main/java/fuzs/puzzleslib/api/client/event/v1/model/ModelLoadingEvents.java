package fuzs.puzzleslib.api.client.event.v1.model;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * For all events it is generally recommended for the call-site to cache returned models themselves, as the event runs
 * for every single model location, even when the models would match per identity.
 */
public final class ModelLoadingEvents {
    public static final EventInvoker<LoadModel> LOAD_MODEL = EventInvoker.lookup(LoadModel.class);
    public static final EventInvoker<ModifyUnbakedModel> MODIFY_UNBAKED_MODEL = EventInvoker.lookup(ModifyUnbakedModel.class);
    public static final EventInvoker<ModifyBakedModel> MODIFY_BAKED_MODEL = EventInvoker.lookup(ModifyBakedModel.class);

    private ModelLoadingEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface LoadModel {

        /**
         * An event that runs for every unbaked model. Allows for replacing the model.
         *
         * @param resourceLocation the resource location for the model
         * @param unbakedModel     the unbaked model, potentially {@code null} for models that remain to be resolved
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<UnbakedModel> onLoadModel(ResourceLocation resourceLocation, @Nullable UnbakedModel unbakedModel);
    }

    @FunctionalInterface
    public interface ModifyUnbakedModel {

        /**
         * An event that runs for every unbaked model. Allows for replacing the model.
         *
         * @param resourceLocation the resource location for the model
         * @param unbakedModel     the unbaked model
         * @param modelState       the settings the model is baking with
         * @param modelBaker       the model baker instance
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<UnbakedModel> onModifyUnbakedModel(ResourceLocation resourceLocation, UnbakedModel unbakedModel, ModelState modelState, ModelBaker modelBaker);
    }

    @FunctionalInterface
    public interface ModifyBakedModel {

        /**
         * An event that runs for every baked model. Allows for replacing the model.
         *
         * @param resourceLocation the resource location for the model
         * @param bakedModel       the baked model
         * @param unbakedModel     the original unbaked model
         * @param modelState       the settings the model is baking with
         * @param modelBaker       the model baker instance
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<BakedModel> onModifyBakedModel(ResourceLocation resourceLocation, BakedModel bakedModel, UnbakedModel unbakedModel, ModelState modelState, ModelBaker modelBaker);
    }
}
