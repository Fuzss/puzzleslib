package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Context for retrieving baked models from the model manager after they've been reloaded.
 * <p>Fired after the resource manager has reloaded models. Does not allow for modifying the models map, for that use {@link DynamicModifyBakingResultContext}.
 *
 * @deprecated migrate to {@link fuzs.puzzleslib.api.client.event.v1.ModelEvents.BakingCompleted}
 */
@Deprecated(forRemoval = true)
public interface DynamicBakingCompletedContext {

    /**
     * @return model manager instance
     */
    ModelManager modelManager();

    /**
     * @return all baked models, the collection is read-only
     */
    Map<ResourceLocation, BakedModel> models();

    /**
     * @return the bakery
     */
    ModelBakery modelBakery();

    /**
     * Retrieves a model from the {@link #modelManager}, allows for using {@link ResourceLocation} instead of {@link net.minecraft.client.resources.model.ModelResourceLocation}.
     *
     * @param identifier model identifier
     * @return the model, possibly empty model instance
     */
    default BakedModel getModel(ResourceLocation identifier) {
        return ClientAbstractions.INSTANCE.getBakedModel(this.modelManager(), identifier);
    }
}
