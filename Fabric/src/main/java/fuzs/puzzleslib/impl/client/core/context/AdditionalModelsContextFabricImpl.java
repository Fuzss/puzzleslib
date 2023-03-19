package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.AdditionalModelsContext;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Objects;
import java.util.function.Consumer;

public final class AdditionalModelsContextFabricImpl implements AdditionalModelsContext {

    @Override
    public void registerAdditionalModel(ResourceLocation model) {
        Objects.requireNonNull(model, "model location is null");
        ModelLoadingRegistry.INSTANCE.registerModelProvider((ResourceManager manager, Consumer<ResourceLocation> out) -> {
            out.accept(model);
        });
    }
}
