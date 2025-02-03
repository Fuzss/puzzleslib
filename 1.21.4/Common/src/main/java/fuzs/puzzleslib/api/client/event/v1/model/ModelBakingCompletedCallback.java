package fuzs.puzzleslib.api.client.event.v1.model;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;

@FunctionalInterface
public interface ModelBakingCompletedCallback {
    EventInvoker<ModelBakingCompletedCallback> EVENT = EventInvoker.lookup(ModelBakingCompletedCallback.class);

    /**
     * Fires after the {@link net.minecraft.server.packs.resources.ResourceManager} has reloaded all models.
     *
     * @param modelManager the model manager
     * @param bakingResult the baking result
     */
    void onModelBakingCompleted(ModelManager modelManager, ModelBakery.BakingResult bakingResult);
}
