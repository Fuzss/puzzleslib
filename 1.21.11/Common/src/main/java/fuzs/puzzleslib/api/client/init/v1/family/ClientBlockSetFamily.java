package fuzs.puzzleslib.api.client.init.v1.family;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.init.v1.ClientWoodTypeRegistry;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * Client side version extension for registration methods in {@link BlockSetFamily}.
 */
public final class ClientBlockSetFamily {
    public static final Map<BlockSetVariant, ChunkSectionLayer> VARIANT_RENDER_TYPE = ImmutableMap.of(BlockSetVariant.DOOR,
            ChunkSectionLayer.CUTOUT,
            BlockSetVariant.TRAPDOOR,
            ChunkSectionLayer.CUTOUT);

    private ClientBlockSetFamily() {
        // NO-OP
    }

    public static void register(BlockSetFamily blockSetFamily) {
        ClientWoodTypeRegistry.registerWoodType(blockSetFamily.getWoodType());
    }

    public static void registerFor(BlockSetFamily blockSetFamily, RenderTypesContext<Block> context, Map<BlockSetVariant, ChunkSectionLayer> variants) {
        blockSetFamily.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            ChunkSectionLayer chunkSectionLayer = variants.get(variant);
            if (chunkSectionLayer != null) {
                context.registerChunkRenderType(holder.value(), chunkSectionLayer);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void registerFor(BlockSetFamily blockSetFamily, EntityRenderersContext context, ModelLayerLocation boatModelLayer, ModelLayerLocation chestBoatModelLayer) {
        context.registerEntityRenderer((EntityType<? extends AbstractBoat>) blockSetFamily.getEntityType(BlockSetVariant.BOAT)
                .value(), (EntityRendererProvider.Context contextX) -> new BoatRenderer(contextX, boatModelLayer));
        context.registerEntityRenderer((EntityType<? extends AbstractBoat>) blockSetFamily.getEntityType(BlockSetVariant.CHEST_BOAT)
                .value(), (EntityRendererProvider.Context contextX) -> new BoatRenderer(contextX, chestBoatModelLayer));
    }
}
