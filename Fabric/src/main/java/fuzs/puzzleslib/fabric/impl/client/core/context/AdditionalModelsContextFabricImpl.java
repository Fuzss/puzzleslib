package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class AdditionalModelsContextFabricImpl implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation... resourceLocations) {
        Objects.requireNonNull(resourceLocations, "resource locations is null");
        Preconditions.checkState(resourceLocations.length > 0, "resource locations is empty");
        ModelLoadingPlugin.register((ModelLoadingPlugin.Context context) -> {
            for (ResourceLocation resourceLocation : resourceLocations) {
                Objects.requireNonNull(resourceLocation, "resource location is null");
                this.registerAdditionalModel(resourceLocation);
                context.addModels(resourceLocation);
            }
        });
    }
}
