package fuzs.puzzleslib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;

/**
 * {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer#renderByItem} as a functional interface
 */
@FunctionalInterface
public interface DynamicBuiltinModelItemRenderer extends ResourceManagerReloadListener {

    /**
     * renders an item stack, kindly copied from Fabric Api :)
     *
     * @param stack             the rendered item stack
     * @param mode              the model transformation mode
     * @param matrices          the matrix stack
     * @param vertexConsumers   the vertex consumer provider
     * @param light             packed lightmap coordinates
     * @param overlay           the overlay UV passed to {@link com.mojang.blaze3d.vertex.VertexConsumer#overlayCoords}
     */
    void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay);

    @Override
    default void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
