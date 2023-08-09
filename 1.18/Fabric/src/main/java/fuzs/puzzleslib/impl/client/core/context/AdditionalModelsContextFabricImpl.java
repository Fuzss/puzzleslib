package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Objects;
import java.util.function.Consumer;

public final class AdditionalModelsContextFabricImpl implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation... models) {
        Objects.requireNonNull(models, "models is null");
        Preconditions.checkPositionIndex(1, models.length, "models is empty");
        ModelLoadingRegistry.INSTANCE.registerModelProvider((ResourceManager manager, Consumer<ResourceLocation> out) -> {
            for (ResourceLocation model : models) {
                Objects.requireNonNull(model, "model is null");
                out.accept(model);
            }
        });
    }
}
