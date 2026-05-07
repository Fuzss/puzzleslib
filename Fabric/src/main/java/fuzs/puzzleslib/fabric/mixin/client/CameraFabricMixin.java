package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.common.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.common.impl.event.data.DefaultedFloat;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(Camera.class)
abstract class CameraFabricMixin {
    @Unique
    private Optional<Float> puzzleslib$zRot = Optional.empty();

    @ModifyArgs(method = "alignWithEntity",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0),
                slice = @Slice(from = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/client/Camera;setPosition(Lnet/minecraft/world/phys/Vec3;)V")))
    public void alignWithEntity(Args args, float partialTicks) {
        this.puzzleslib$zRot = Optional.empty();
        MutableFloat pitch = MutableFloat.fromEvent((Float xRot) -> args.set(1, xRot), () -> args.get(1));
        MutableFloat yaw = MutableFloat.fromEvent((Float yRot) -> args.set(0, yRot), () -> args.get(0));
        MutableFloat roll = MutableFloat.fromEvent((Float zRot) -> this.puzzleslib$zRot = Optional.of(zRot),
                () -> this.puzzleslib$zRot.orElse(0.0F));
        FabricRendererEvents.COMPUTE_CAMERA_ANGLES.invoker()
                .onComputeCameraAngles(Camera.class.cast(this), partialTicks, pitch, yaw, roll);
    }

    @ModifyArg(method = "setRotation",
               at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"),
               index = 2)
    protected float setRotation(float zRot) {
        if (this.puzzleslib$zRot.isPresent()) {
            return -this.puzzleslib$zRot.get() * Mth.DEG_TO_RAD;
        } else {
            return zRot;
        }
    }

    @ModifyReturnValue(method = "modifyFovBasedOnDeathOrFluid", at = @At("TAIL"))
    private float modifyFovBasedOnDeathOrFluid(float fov, @Local(ordinal = 0, argsOnly = true) float partialTicks) {
        DefaultedFloat fieldOfView = DefaultedFloat.fromValue(fov);
        FabricRendererEvents.COMPUTE_FIELD_OF_VIEW.invoker()
                .onComputeFieldOfView(Camera.class.cast(this), partialTicks, fieldOfView);
        return fieldOfView.getAsOptionalFloat().orElse(fov);
    }
}
