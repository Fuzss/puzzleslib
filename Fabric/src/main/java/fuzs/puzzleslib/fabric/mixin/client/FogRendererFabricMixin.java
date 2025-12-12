package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
abstract class FogRendererFabricMixin {

    @ModifyReturnValue(method = "computeFogColor", at = @At("TAIL"))
    private static Vector4f computeFogColor(Vector4f vector4f, Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float darkenWorldAmount) {
        // this could also be an injection at tail, but that does not stack with other injectors
        Minecraft minecraft = Minecraft.getInstance();
        MutableFloat red = MutableFloat.fromEvent((Float x) -> vector4f.x = x, vector4f::x);
        MutableFloat green = MutableFloat.fromEvent((Float y) -> vector4f.y = y, vector4f::y);
        MutableFloat blue = MutableFloat.fromEvent((Float z) -> vector4f.z = z, vector4f::z);
        FabricRendererEvents.FOG_COLOR.invoker().onComputeFogColor(camera, partialTick, red, green, blue);
        return vector4f;
    }

    @Inject(method = "setupFog",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;setupFog(Lnet/minecraft/client/renderer/fog/FogData;Lnet/minecraft/client/Camera;Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/DeltaTracker;)V"))
    public void setupFog(CallbackInfoReturnable<Vector4f> callback, @Local FogEnvironment fogEnvironment, @Share(
            "fogEnvironment") LocalRef<FogEnvironment> fogEnvironmentRef) {
        fogEnvironmentRef.set(fogEnvironment);
    }

    @Inject(method = "setupFog",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER))
    public void setupFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> callback, @Local FogType fogType, @Local FogData fogData, @Share(
            "fogEnvironment") LocalRef<@Nullable FogEnvironment> fogEnvironmentRef) {
        FabricRendererEvents.SETUP_FOG.invoker()
                .onSetupFog(camera,
                        deltaTracker.getGameTimeDeltaPartialTick(false),
                        fogEnvironmentRef.get(),
                        fogType,
                        fogData);
    }
}
