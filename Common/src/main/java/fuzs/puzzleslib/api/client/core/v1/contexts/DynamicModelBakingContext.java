package fuzs.puzzleslib.api.client.core.v1.contexts;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Base context for {@link ModelBakingListenersContext}.
 */
public interface DynamicModelBakingContext {

    /**
     * Context for modifying baked models right after they've been reloaded.
     */
    @FunctionalInterface
    interface ModifyBakingResult extends DynamicModelBakingContext {

        /**
         * Fired when the resource manager is reloading models and models have been baked, but before they are passed on for caching.
         *
         * @param models      all baked models for modifying
         * @param modelBakery the bakery
         */
        void onModifyBakingResult(Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery);
    }

    /**
     * Context for retrieving baked models from the model manager after they've been reloaded.
     */
    @FunctionalInterface
    interface BakingCompleted extends DynamicModelBakingContext {

        /**
         * Fired after the resource manager has reloaded models. Does not allow for modifying the models map, for that use {@link ModifyBakingResult}.
         *
         * @param modelManager model manager instance
         * @param models       all baked models, the collection is read-only
         * @param modelBakery  the bakery
         */
        void onBakingCompleted(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery);
    }
}
