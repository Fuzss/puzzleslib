package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;

/**
 * get access to registering additional models
 */
@FunctionalInterface
public interface AdditionalModelsContext {

    /**
     * register a model that is referenced nowhere and would normally not be loaded
     *
     * @param model the models location
     */
    void registerAdditionalModel(ResourceLocation model);
}
