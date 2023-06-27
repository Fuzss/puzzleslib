package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Context for modifying baked models right after they've been reloaded.
 * <p>Fired when the resource manager is reloading models and models have been baked, but before they are passed on for caching.
 */
public interface DynamicModifyBakingResultContext {

    /**
     * @return all baked models for modifying
     */
    Map<ResourceLocation, BakedModel> models();

    /**
     * @return the bakery
     */
    ModelBakery modelBakery();
}
