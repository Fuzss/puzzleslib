package fuzs.puzzleslib.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.client.renderer.v1.special.SpecialModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class SpecialBuiltInItemRenderer<T> implements ReloadingBuiltInItemRenderer {
    private final SpecialModelRenderer.Unbaked<T> unbaked;
    private SpecialModelRenderer<T> modelRenderer;

    public SpecialBuiltInItemRenderer(SpecialModelRenderer.Unbaked<T> unbaked) {
        this.unbaked = unbaked;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int lightCoords, int overlayCoords) {
        Objects.requireNonNull(this.modelRenderer, "model renderer is null");
        this.modelRenderer.render(this.modelRenderer.extractArgument(itemStack),
                poseStack,
                bufferSource,
                lightCoords,
                overlayCoords,
                itemStack.hasFoil());
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.modelRenderer = this.unbaked.bake(Minecraft.getInstance().getEntityModels());
    }
}
