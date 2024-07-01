package fuzs.puzzleslib.api.client.init.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Provides {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer#renderByItem} as a functional
 * interface.
 */
@FunctionalInterface
public interface BuiltinItemRenderer {

    /**
     * Renders an item stack.
     * <p>Kindly copied from Fabric Api :)
     *
     * @param itemStack          the rendered item stack
     * @param itemDisplayContext the model transformation mode
     * @param poseStack          the matrix stack
     * @param multiBufferSource  the vertex consumer provider
     * @param packedLight        packed light map coordinates
     * @param packedOverlay      the overlay UV passed to
     *                           {@link com.mojang.blaze3d.vertex.VertexConsumer#overlayCoords}
     */
    void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay);
}
