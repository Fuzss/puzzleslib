package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.function.Function;

/**
 * Copied from Minecraft 26.1.
 */
public class TypedBoatRenderer extends BoatRenderer {

    public TypedBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelId, Function<ModelPart, ListModel<Boat>> modelFactory) {
        super(context, false);
        this.boatResources = ImmutableMap.of(Boat.Type.OAK, Pair.of(modelId.getModel().withPath((String path) -> {
            return "textures/entity/" + path + ".png";
        }), modelFactory.apply(context.bakeLayer(modelId))));
    }
}
