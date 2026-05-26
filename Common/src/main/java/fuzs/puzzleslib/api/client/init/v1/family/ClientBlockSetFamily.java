package fuzs.puzzleslib.api.client.init.v1.family;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.init.v1.ClientWoodTypeRegistry;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.impl.client.init.TypedBoatRenderer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * Client side version extension for registration methods in {@link BlockSetFamily}.
 */
public final class ClientBlockSetFamily {
    public static final Map<BlockSetVariant, RenderType> VARIANT_RENDER_TYPE = ImmutableMap.of(BlockSetVariant.DOOR,
            RenderType.cutout(),
            BlockSetVariant.TRAPDOOR,
            RenderType.cutout());

    private ClientBlockSetFamily() {
        // NO-OP
    }

    public static void register(BlockSetFamily blockSetFamily) {
        ClientWoodTypeRegistry.registerWoodType(blockSetFamily.getWoodType());
    }

    public static void registerFor(BlockSetFamily blockSetFamily, RenderTypesContext<Block> context, Map<BlockSetVariant, RenderType> variants) {
        blockSetFamily.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            RenderType chunkSectionLayer = variants.get(variant);
            if (chunkSectionLayer != null) {
                context.registerRenderType(chunkSectionLayer, holder.value());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void registerFor(BlockSetFamily blockSetFamily, EntityRenderersContext context, ModelLayerLocation boatModelLayer, ModelLayerLocation chestBoatModelLayer) {
        context.registerEntityRenderer((EntityType<? extends Boat>) blockSetFamily.getEntityType(BlockSetVariant.BOAT)
                        .value(),
                (EntityRendererProvider.Context contextX) -> new TypedBoatRenderer(contextX,
                        boatModelLayer,
                        BoatModel::new));
        context.registerEntityRenderer((EntityType<? extends Boat>) blockSetFamily.getEntityType(BlockSetVariant.CHEST_BOAT)
                        .value(),
                (EntityRendererProvider.Context contextX) -> new TypedBoatRenderer(contextX,
                        chestBoatModelLayer,
                        ChestBoatModel::new));
    }
}
