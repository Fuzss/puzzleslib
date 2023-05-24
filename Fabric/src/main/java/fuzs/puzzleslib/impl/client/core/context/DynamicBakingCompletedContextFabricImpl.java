package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.DynamicBakingCompletedContext;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record DynamicBakingCompletedContextFabricImpl(ModelManager modelManager,
                                                      Map<ResourceLocation, BakedModel> models,
                                                      ModelBakery modelBakery) implements DynamicBakingCompletedContext {

    @Override
    public BakedModel getModel(ResourceLocation identifier) {
        return BakedModelManagerHelper.getModel(this.modelManager(), identifier);
    }
}
