package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import fuzs.puzzleslib.fabric.mixin.client.accessor.CameraFabricAccessor;
import fuzs.puzzleslib.fabric.impl.client.event.EntitySpectatorShaderRegistryImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererFabricMixin {
    @Shadow
    @Final
    private Camera mainCamera;
    @Shadow
    @Nullable
    private PostChain postEffect;

    @Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
    public void checkEntityPostEffect(@Nullable Entity entity, CallbackInfo callback) {
        if (this.postEffect == null && entity != null) {
            EntitySpectatorShaderRegistryImpl.getEntityShader(entity).ifPresent(this::loadEffect);
        }
    }

    @Shadow
    private void loadEffect(ResourceLocation resourceLocation) {
        throw new RuntimeException();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    public void renderLevel(float partialTicks, long finishTimeNano, PoseStack matrixStack, CallbackInfo callback) {
        MutableFloat pitch = MutableFloat.fromEvent(((CameraFabricAccessor) this.mainCamera)::puzzleslib$setXRot, this.mainCamera::getXRot);
        MutableFloat yaw = MutableFloat.fromEvent(((CameraFabricAccessor) this.mainCamera)::puzzleslib$setYRot, this.mainCamera::getYRot);
        DefaultedFloat roll = DefaultedFloat.fromValue(0.0F);
        FabricRendererEvents.COMPUTE_CAMERA_ANGLES.invoker().onComputeCameraAngles(GameRenderer.class.cast(this), this.mainCamera, partialTicks, pitch, yaw, roll);
        roll.getAsOptionalFloat().ifPresent(f -> matrixStack.mulPose(Axis.ZP.rotationDegrees(f)));
    }
}
