package fuzs.puzzleslib.common.api.client.init.v1.family;

import fuzs.puzzleslib.common.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

/**
 * Client side version extension for registration methods in {@link BlockSetFamily}.
 */
public final class ClientBlockSetFamily {

    private ClientBlockSetFamily() {
        // NO-OP
    }

    @SuppressWarnings("unchecked")
    public static void registerFor(BlockSetFamily blockSetFamily, EntityRenderersContext context, ModelLayerLocation boatModelLayer, ModelLayerLocation chestBoatModelLayer) {
        context.registerEntityRenderer((EntityType<? extends AbstractBoat>) blockSetFamily.getEntityType(BlockSetVariant.BOAT)
                .value(), (EntityRendererProvider.Context contextX) -> new BoatRenderer(contextX, boatModelLayer));
        context.registerEntityRenderer((EntityType<? extends AbstractBoat>) blockSetFamily.getEntityType(BlockSetVariant.CHEST_BOAT)
                .value(), (EntityRendererProvider.Context contextX) -> new BoatRenderer(contextX, chestBoatModelLayer));
    }
}
