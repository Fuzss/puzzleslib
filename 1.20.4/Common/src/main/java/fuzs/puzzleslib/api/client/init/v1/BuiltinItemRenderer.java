package fuzs.puzzleslib.api.client.init.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Provides {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer#renderByItem} as a functional interface.
 */
@FunctionalInterface
public interface BuiltinItemRenderer {

    /**
     * Renders an item stack.
     * <p>Kindly copied from Fabric Api :)
     *
     * @param stack             the rendered item stack
     * @param mode              the model transformation mode
     * @param matrices          the matrix stack
     * @param vertexConsumers   the vertex consumer provider
     * @param light             packed light map coordinates
     * @param overlay           the overlay UV passed to {@link com.mojang.blaze3d.vertex.VertexConsumer#overlayCoords}
     */
    void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay);
}
