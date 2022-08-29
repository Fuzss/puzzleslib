package fuzs.puzzleslib.impl.client.resources.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface ModelManagerExtension {

    Map<ResourceLocation, BakedModel> puzzleslib_getBakedRegistry();

    ModelBakery puzzleslib_getModelBakery();
}
