package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeModelBakery;

import java.util.Objects;

public record AdditionalModelsContextForgeImpl() implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation... models) {
        Objects.requireNonNull(models, "models is null");
        Preconditions.checkPositionIndex(1, models.length, "models is empty");
        for (ResourceLocation model : models) {
            Objects.requireNonNull(model, "model is null");
            ForgeModelBakery.addSpecialModel(model);
        }
    }
}
