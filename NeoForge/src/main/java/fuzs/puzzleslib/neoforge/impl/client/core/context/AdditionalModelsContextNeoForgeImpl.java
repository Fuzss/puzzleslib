package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Objects;

public record AdditionalModelsContextNeoForgeImpl(ModelEvent.RegisterAdditional evt) implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        this.evt.register(resourceLocation);
    }
}
