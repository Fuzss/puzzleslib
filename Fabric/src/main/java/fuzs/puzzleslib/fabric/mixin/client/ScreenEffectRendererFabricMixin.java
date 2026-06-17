package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
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
    private SpriteGetter sprites;

    @WrapWithCondition(method = "submit",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;submitBlockSprite(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"))
    public boolean submit(TextureAtlasSprite sprite, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int color, @Local BlockState blockState) {
        return FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(this.minecraft.player, poseStack, submitNodeCollector, blockState, this.sprites)
                .isPass();
    }

    @WrapWithCondition(method = "submit",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;submitWater(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    public boolean submit(Minecraft minecraft, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        return FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(minecraft.player,
                        poseStack,
                        submitNodeCollector,
                        Blocks.WATER.defaultBlockState(),
                        this.sprites)
                .isPass();
    }

    @WrapWithCondition(method = "submit",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;submitFire(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    public boolean submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, TextureAtlasSprite sprite) {
        return FabricRendererEvents.RENDER_BLOCK_OVERLAY.invoker()
                .onRenderBlockOverlay(this.minecraft.player,
                        poseStack,
                        submitNodeCollector,
                        Blocks.FIRE.defaultBlockState(),
                        this.sprites)
                .isPass();
    }
}
