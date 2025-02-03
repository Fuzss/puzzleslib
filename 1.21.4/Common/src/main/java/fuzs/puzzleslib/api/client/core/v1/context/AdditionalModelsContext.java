package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Register additional models.
 */
@FunctionalInterface
public interface AdditionalModelsContext {

    /**
     * Register a model that is referenced nowhere and would normally not be loaded.
     *
     * @param resourceLocation the resource location for an additional model
     */
    default void registerAdditionalModel(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        this.registerAdditionalModel(new ResourceLocation[]{resourceLocation});
    }

    /**
     * Register a model that is referenced nowhere and would normally not be loaded.
     *
     * @param resourceLocations the resource locations for an additional models
     */
    void registerAdditionalModel(ResourceLocation... resourceLocations);
}
