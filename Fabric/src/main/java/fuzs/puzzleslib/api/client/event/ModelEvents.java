package fuzs.puzzleslib.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class ModelEvents {
    public static final Event<BakingCompleted> BAKING_COMPLETED = EventFactory.createArrayBacked(BakingCompleted.class, callbacks -> (ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) -> {
        for (BakingCompleted callback : callbacks) {
            callback.onBakingCompleted(modelManager, models, modelBakery);
        }
    });

    @FunctionalInterface
    public interface BakingCompleted {

        /**
         * fired when the resource manager is reloading, called after models have been baked, but before they're passed on
         *
         * @param modelManager model manager instance
         * @param models       all baked models
         * @param modelBakery  the bakery
         */
        void onBakingCompleted(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery);
    }
}