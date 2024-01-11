package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class AdditionalModelsContextFabricImpl implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation... models) {
        Objects.requireNonNull(models, "models is null");
        Preconditions.checkPositionIndex(1, models.length, "models is empty");
        ModelLoadingPlugin.register((ModelLoadingPlugin.Context context) -> {
            for (ResourceLocation model : models) {
                Objects.requireNonNull(model, "model is null");
                context.addModels(model);
            }
        });
    }
}
