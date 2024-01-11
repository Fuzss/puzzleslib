package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
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
    private static @Nullable BlockState renderScreenEffect(@Nullable BlockState blockState, Minecraft minecraft, PoseStack poseStack) {
        EventResult result = FabricClientEvents.RENDER_BLOCK_OVERLAY.invoker().onRenderBlockOverlay(minecraft.player, poseStack, blockState);
        return result.isInterrupt() ? null : blockState;
    }

    @Inject(method = "renderWater", at = @At("HEAD"), cancellable = true)
    private static void renderWater(Minecraft minecraft, PoseStack poseStack, CallbackInfo callback) {
        EventResult result = FabricClientEvents.RENDER_BLOCK_OVERLAY.invoker().onRenderBlockOverlay(minecraft.player, poseStack, Blocks.WATER.defaultBlockState());
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void renderFire(Minecraft minecraft, PoseStack poseStack, CallbackInfo callback) {
        EventResult result = FabricClientEvents.RENDER_BLOCK_OVERLAY.invoker().onRenderBlockOverlay(minecraft.player, poseStack, Blocks.FIRE.defaultBlockState());
        if (result.isInterrupt()) callback.cancel();
    }
}
