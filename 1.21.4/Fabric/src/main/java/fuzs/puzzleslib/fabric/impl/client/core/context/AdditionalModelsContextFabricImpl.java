package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record AdditionalModelsContextFabricImpl(ModelLoadingPlugin.Context context) implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        this.context.addModels(resourceLocation);
    }
}
