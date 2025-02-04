package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Preconditions;
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
    void registerAdditionalModel(ResourceLocation resourceLocation);

    /**
     * Register a model that is referenced nowhere and would normally not be loaded.
     *
     * @param resourceLocations the resource locations for an additional models
     */
    default void registerAdditionalModel(ResourceLocation... resourceLocations) {
        Objects.requireNonNull(resourceLocations, "resource locations is null");
        Preconditions.checkState(resourceLocations.length > 0, "resource locations is empty");
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.registerAdditionalModel(resourceLocation);
        }
    }
}
