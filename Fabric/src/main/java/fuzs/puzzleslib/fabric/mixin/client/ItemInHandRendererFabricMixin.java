package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererFabricMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderArmWithItem(AbstractClientPlayer abstractClientPlayer, float partialTick, float interpolatedPitch, InteractionHand interactionHand, float swingProgress, ItemStack itemStack, float equipProgress, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo callback) {
        EventResult result = FabricClientEvents.RENDER_HAND.invoker().onRenderHand(abstractClientPlayer, interactionHand, itemStack, poseStack, multiBufferSource, packedLight, partialTick, interpolatedPitch, swingProgress, equipProgress);
        if (result.isInterrupt()) callback.cancel();
    }
}
