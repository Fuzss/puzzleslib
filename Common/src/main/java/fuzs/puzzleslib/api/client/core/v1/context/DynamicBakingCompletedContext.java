package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Context for retrieving baked models from the model manager after they've been reloaded.
 * <p>Fired after the resource manager has reloaded models. Does not allow for modifying the models map, for that use {@link DynamicModifyBakingResultContext}.
 */
@ApiStatus.NonExtendable
public abstract class DynamicBakingCompletedContext {
    /**
     * model manager instance
     */
    private final ModelManager modelManager;
    /**
     * all baked models, the collection is read-only
     */
    private final Map<ResourceLocation, BakedModel> models;
    /**
     * the bakery
     */
    private final ModelBakery modelBakery;

    /**
     * internal constructor
     */
    @ApiStatus.Internal
    public DynamicBakingCompletedContext(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
        this.modelManager = modelManager;
        this.models = models;
        this.modelBakery = modelBakery;
    }

    /**
     * @return model manager instance
     */
    public ModelManager modelManager() {
        return this.modelManager;
    }

    /**
     * @return all baked models, the collection is read-only
     */
    public Map<ResourceLocation, BakedModel> models() {
        return this.models;
    }

    /**
     * @return the bakery
     */
    public ModelBakery modelBakery() {
        return this.modelBakery;
    }

    /**
     * Retrieves a model from the {@link #modelManager}, allows for using {@link ResourceLocation} instead of {@link net.minecraft.client.resources.model.ModelResourceLocation}.
     *
     * @param identifier model identifier
     * @return the model, possibly empty model instance
     */
    public abstract BakedModel getModel(ResourceLocation identifier);
}
