package fuzs.puzzleslib.api.client.renderer.v1.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Copied from Minecraft 1.21.8.
 */
public interface NoDataSpecialModelRenderer extends SpecialModelRenderer<Void> {
    @Override
    default @Nullable Void extractArgument(ItemStack stack) {
        return null;
    }

    @Override
    default void render(@Nullable Void argument, PoseStack poseStack, MultiBufferSource bufferSource, int lightCoords, int overlayCoords, boolean hasFoil) {
        this.render(poseStack, bufferSource, lightCoords, overlayCoords, hasFoil);
    }

    void render(PoseStack poseStack, MultiBufferSource bufferSource, int lightCoords, int overlayCoords, boolean hasFoil);

    interface Unbaked extends SpecialModelRenderer.Unbaked<Void> {
        @Override
        MapCodec<? extends Unbaked> type();
    }
}
