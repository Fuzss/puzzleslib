package fuzs.puzzleslib.client.resources.model;

import fuzs.puzzleslib.client.core.ClientModConstructor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * context with useful objects passed to mods after baked models have been reloaded
 */
public abstract class DynamicModelBakingContext {
    /**
     * the model manager
     */
    public final ModelManager modelManager;
    /**
     * map of all baked models, useful to add or replace models
     */
    public final Map<ResourceLocation, BakedModel> models;
    /**
     * the bakery
     */
    public final ModelBakery modelBakery;

    /**
     * @param modelManager      the model manager
     * @param models            map of all baked models, useful to add or replace models
     * @param modelBakery       the bakery
     */
    public DynamicModelBakingContext(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
        this.modelManager = modelManager;
        this.models = models;
        this.modelBakery = modelBakery;
    }

    /**
     * bake a custom model, the location has ideally been registered in {@link ClientModConstructor#onRegisterAdditionalModels}
     *
     * <p>implementation on Fabric does not need to create the unbaked model, it's done automatically when registering an additional model location
     *
     * @param modelLocation     location to get {@link net.minecraft.client.resources.model.UnbakedModel} from
     * @return                  the baked model, possible from {@link ModelBakery#MISSING_MODEL_LOCATION}
     */
    public abstract BakedModel bakeModel(ResourceLocation modelLocation);
}
