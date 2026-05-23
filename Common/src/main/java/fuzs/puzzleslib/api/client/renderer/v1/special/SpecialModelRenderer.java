package fuzs.puzzleslib.api.client.renderer.v1.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Copied from Minecraft 1.21.8.
 */
public interface SpecialModelRenderer<T> {
    void render(@Nullable T argument, PoseStack poseStack, MultiBufferSource bufferSource, int lightCoords, int overlayCoords, boolean hasFoil);

    @Nullable T extractArgument(ItemStack itemStack);

    interface Unbaked<T> {
        @Nullable SpecialModelRenderer<T> bake(EntityModelSet modelSet);

        MapCodec<? extends Unbaked<T>> type();
    }
}
