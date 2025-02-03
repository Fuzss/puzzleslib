package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Objects;

public record AdditionalModelsContextNeoForgeImpl(ModelEvent.RegisterAdditional evt) implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation... resourceLocations) {
        Objects.requireNonNull(resourceLocations, "resource locations is null");
        Preconditions.checkState(resourceLocations.length > 0, "resource locations is empty");
        for (ResourceLocation resourceLocation : resourceLocations) {
            Objects.requireNonNull(resourceLocation, "resource location is null");
            this.registerAdditionalModel(resourceLocation);
            this.evt.register(resourceLocation);
        }
    }
}
