package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
abstract class ScreenEffectRendererFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private MaterialSet materials;

    @WrapWithCondition(method = "renderScreenEffect",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    public boolean renderScreenEffect(TextureAtlasSprite textureAtlasSprite, PoseStack poseStack, MultiBufferSource bufferSource, @Local BlockState blockState) {
        return FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(this.minecraft.player, poseStack, bufferSource, blockState, this.materials)
                .isPass();
    }

    @WrapWithCondition(method = "renderScreenEffect",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderWater(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    public boolean renderScreenEffect(Minecraft minecraft, PoseStack poseStack, MultiBufferSource bufferSource) {
        return FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(minecraft.player,
                        poseStack,
                        bufferSource,
                        Blocks.WATER.defaultBlockState(),
                        this.materials)
                .isPass();
    }

    @WrapWithCondition(method = "renderScreenEffect",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderFire(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    public boolean renderScreenEffect(PoseStack poseStack, MultiBufferSource bufferSource, TextureAtlasSprite textureAtlasSprite) {
        return FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(this.minecraft.player,
                        poseStack,
                        bufferSource,
                        Blocks.FIRE.defaultBlockState(),
                        this.materials)
                .isPass();
    }
}
