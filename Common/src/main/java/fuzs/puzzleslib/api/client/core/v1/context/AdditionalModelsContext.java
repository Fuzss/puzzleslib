package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;

/**
 * Register additional block models.
 */
@FunctionalInterface
public interface AdditionalModelsContext {

    /**
     * Register a model that is referenced nowhere and would normally not be loaded.
     *
     * @param models model locations for models
     */
    void registerAdditionalModel(ResourceLocation... models);
}
