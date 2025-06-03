package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.FogShape;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
abstract class FogRendererFabricMixin {

    @ModifyReturnValue(method = "computeFogColor", at = @At("TAIL"))
    private static Vector4f computeFogColor(Vector4f vector4f, Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float darkenWorldAmount) {
        // this could also be an injection at tail, but that does not stack with other injectors
        Minecraft minecraft = Minecraft.getInstance();
        MutableFloat red = MutableFloat.fromEvent(x -> vector4f.x = x, vector4f::x);
        MutableFloat green = MutableFloat.fromEvent(y -> vector4f.y = y, vector4f::y);
        MutableFloat blue = MutableFloat.fromEvent(z -> vector4f.z = z, vector4f::z);
        FabricRendererEvents.COMPUTE_FOG_COLOR.invoker().onComputeFogColor(minecraft.gameRenderer, camera, partialTick,
                red, green, blue
        );
        return vector4f;
    }

    @ModifyReturnValue(method = "setupFog", at = @At("TAIL"))
    private static FogParameters setupFog(FogParameters fogParameters, Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float farPlaneDistance, boolean isFoggy, float partialTick, @Local FogType fogType) {
        Minecraft minecraft = Minecraft.getInstance();
        FogParameters[] fogParametersObj = new FogParameters[]{fogParameters};
        MutableFloat fogStart = MutableFloat.fromEvent(startValue -> {
            FogParameters fogParametersInst = fogParametersObj[0];
            fogParametersObj[0] = new FogParameters(startValue, fogParametersInst.end(), fogParametersInst.shape(),
                    fogParametersInst.red(), fogParametersInst.green(), fogParametersInst.blue(),
                    fogParametersInst.alpha()
            );
        }, () -> fogParametersObj[0].start());
        MutableFloat fogEnd = MutableFloat.fromEvent(endValue -> {
            FogParameters fogParametersInst = fogParametersObj[0];
            fogParametersObj[0] = new FogParameters(fogParametersInst.start(), endValue, fogParametersInst.shape(),
                    fogParametersInst.red(), fogParametersInst.green(), fogParametersInst.blue(),
                    fogParametersInst.alpha()
            );
        }, () -> fogParametersObj[0].end());
        MutableValue<FogShape> fogShape = MutableValue.fromEvent(fogShapeValue -> {
            FogParameters fogParametersInst = fogParametersObj[0];
            fogParametersObj[0] = new FogParameters(fogParametersInst.start(), fogParametersInst.end(), fogShapeValue,
                    fogParametersInst.red(), fogParametersInst.green(), fogParametersInst.blue(),
                    fogParametersInst.alpha()
            );
        }, () -> fogParametersObj[0].shape());
        FabricRendererEvents.RENDER_FOG.invoker().onRenderFog(minecraft.gameRenderer, camera, partialTick, fogMode,
                fogType, fogStart, fogEnd, fogShape
        );
        return fogParametersObj[0];
    }
}
