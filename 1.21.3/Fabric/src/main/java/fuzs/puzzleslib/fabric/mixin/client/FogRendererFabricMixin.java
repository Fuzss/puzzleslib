package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FogRenderer.class)
abstract class FogRendererFabricMixin {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", ordinal = 1, remap = false))
    private static void setupColor(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo callback) {
        Minecraft minecraft = Minecraft.getInstance();
        MutableFloat red = MutableFloat.fromEvent(t -> fogRed = t, () -> fogRed);
        MutableFloat green = MutableFloat.fromEvent(t -> fogGreen = t, () -> fogGreen);
        MutableFloat blue = MutableFloat.fromEvent(t -> fogBlue = t, () -> fogBlue);
        FabricRendererEvents.COMPUTE_FOG_COLOR.invoker().onComputeFogColor(minecraft.gameRenderer, activeRenderInfo, partialTicks, red, green, blue);
    }

    @Inject(method = "setupFog", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void setupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean bl, float f, CallbackInfo callback, FogType fogType) {
        Minecraft minecraft = Minecraft.getInstance();
        MutableFloat fogStart = MutableFloat.fromEvent(RenderSystem::setShaderFogStart, RenderSystem::getShaderFogStart);
        MutableFloat fogEnd = MutableFloat.fromEvent(RenderSystem::setShaderFogEnd, RenderSystem::getShaderFogEnd);
        MutableValue<FogShape> fogShape = MutableValue.fromEvent(RenderSystem::setShaderFogShape, RenderSystem::getShaderFogShape);
        FabricRendererEvents.RENDER_FOG.invoker().onRenderFog(minecraft.gameRenderer, camera, f, fogMode, fogType, fogStart, fogEnd, fogShape);
    }
}
