package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
abstract class CameraFabricMixin {
    @Unique
    private float puzzleslib$zRot;

    @WrapOperation(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0)
    )
    public void setup(Camera camera, float xRot, float yRot, Operation<Void> operation, BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        MutableFloat pitch = MutableFloat.fromValue(yRot);
        MutableFloat yaw = MutableFloat.fromValue(xRot);
        MutableFloat roll = MutableFloat.fromValue(0.0F);
        FabricRendererEvents.COMPUTE_CAMERA_ANGLES.invoker().onComputeCameraAngles(minecraft.gameRenderer, camera,
                partialTick, pitch, yaw, roll
        );
        this.puzzleslib$zRot = roll.getAsFloat();
        operation.call(camera, yaw.getAsFloat(), pitch.getAsFloat());
    }

    @ModifyExpressionValue(method = "setRotation", at = @At(value = "CONSTANT", args = "floatValue=0.0"))
    protected float setRotation(float zRot) {
        return -this.puzzleslib$zRot * Mth.DEG_TO_RAD;
    }
}
