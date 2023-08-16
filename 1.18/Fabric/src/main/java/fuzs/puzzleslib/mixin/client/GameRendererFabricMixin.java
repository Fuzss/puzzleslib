package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.impl.client.core.CoreShaderRegistrationCallback;
import fuzs.puzzleslib.impl.client.core.FabricShaderProgram;
import fuzs.puzzleslib.impl.client.event.EntitySpectatorShaderRegistryImpl;
import fuzs.puzzleslib.mixin.client.accessor.CameraFabricAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
abstract class GameRendererFabricMixin {
    @Shadow
    @Final
    private Camera mainCamera;
    @Shadow
    @Nullable
    private PostChain postEffect;

    @ModifyVariable(method = "reloadShaders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, remap = false), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=rendertype_crumbling")), ordinal = 1)
    List<Pair<ShaderInstance, Consumer<ShaderInstance>>> reloadShaders(List<Pair<ShaderInstance, Consumer<ShaderInstance>>> programs, ResourceManager factory) throws IOException {
        CoreShaderRegistrationCallback.RegistrationContext context = (id, vertexFormat, loadCallback) -> {
            ShaderInstance program = new FabricShaderProgram(factory, id, vertexFormat);
            programs.add(Pair.of(program, loadCallback));
        };
        CoreShaderRegistrationCallback.EVENT.invoker().registerShaders(context);
        return programs;
    }

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
        FabricClientEvents.COMPUTE_CAMERA_ANGLES.invoker().onComputeCameraAngles(GameRenderer.class.cast(this), this.mainCamera, partialTicks, pitch, yaw, roll);
        roll.getAsOptionalFloat().ifPresent(f -> matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f)));
    }
}
