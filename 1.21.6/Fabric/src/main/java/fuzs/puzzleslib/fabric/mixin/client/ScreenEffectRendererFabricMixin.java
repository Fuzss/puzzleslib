package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
abstract class ScreenEffectRendererFabricMixin {

    @ModifyVariable(method = "renderScreenEffect", at = @At("STORE"))
    private static @Nullable BlockState renderScreenEffect(@Nullable BlockState blockState, Minecraft minecraft, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (blockState != null) {
            EventResult result = FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                    .onRenderBlockOverlay(minecraft.player, poseStack, bufferSource, blockState);
            return result.isInterrupt() ? null : blockState;
        } else {
            return null;
        }
    }

    @Inject(method = "renderWater", at = @At("HEAD"), cancellable = true)
    private static void renderWater(Minecraft minecraft, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo callback) {
        EventResult result = FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(minecraft.player, poseStack, bufferSource, Blocks.WATER.defaultBlockState());
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void renderFire(PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo callback) {
        EventResult result = FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(Minecraft.getInstance().player,
                        poseStack,
                        bufferSource,
                        Blocks.FIRE.defaultBlockState());
        if (result.isInterrupt()) callback.cancel();
    }
}
