package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.context.DynamicModifyBakingResultContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record DynamicModifyBakingResultContextImpl(Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) implements DynamicModifyBakingResultContext {

}
