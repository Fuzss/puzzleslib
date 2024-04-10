package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;

public record AdditionalModelsContextNeoForgeImpl(Consumer<ResourceLocation> consumer) implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation... models) {
        Objects.requireNonNull(models, "models is null");
        Preconditions.checkState(models.length > 0, "models is empty");
        for (ResourceLocation model : models) {
            Objects.requireNonNull(model, "model is null");
            this.consumer.accept(model);
        }
    }
}
