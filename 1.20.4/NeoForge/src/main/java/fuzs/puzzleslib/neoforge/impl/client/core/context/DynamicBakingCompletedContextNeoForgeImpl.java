package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.DynamicBakingCompletedContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record DynamicBakingCompletedContextNeoForgeImpl(ModelManager modelManager,
                                                        Map<ResourceLocation, BakedModel> models,
                                                        ModelBakery modelBakery) implements DynamicBakingCompletedContext {

}
