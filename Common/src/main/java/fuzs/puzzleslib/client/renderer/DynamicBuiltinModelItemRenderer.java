package fuzs.puzzleslib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

/**
 * {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer} as a functional interface
 */
@FunctionalInterface
public interface DynamicBuiltinModelItemRenderer {

    /**
     * Renders an item stack, kindly copied from Fabric Api :)
     *
     * @param stack             the rendered item stack
     * @param mode              the model transformation mode
     * @param matrices          the matrix stack
     * @param vertexConsumers   the vertex consumer provider
     * @param light             packed lightmap coordinates
     * @param overlay           the overlay UV passed to {@link com.mojang.blaze3d.vertex.VertexConsumer#overlayCoords}
     */
    void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay);
}
