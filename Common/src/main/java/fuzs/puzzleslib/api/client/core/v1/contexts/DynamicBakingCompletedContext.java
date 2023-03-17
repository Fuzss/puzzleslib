package fuzs.puzzleslib.api.client.core.v1.contexts;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Context for retrieving baked models from the model manager after they've been reloaded.
 * <p>Fired after the resource manager has reloaded models. Does not allow for modifying the models map, for that use {@link DynamicModifyBakingResultContext}.
 *
 * @param modelManager model manager instance
 * @param models       all baked models, the collection is read-only
 * @param modelBakery  the bakery
 */
public record DynamicBakingCompletedContext(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {

}
