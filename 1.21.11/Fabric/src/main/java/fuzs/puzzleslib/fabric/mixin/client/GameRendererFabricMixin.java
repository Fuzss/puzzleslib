package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import fuzs.puzzleslib.fabric.impl.client.core.context.EntitySpectatorShadersContextFabricImpl;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
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
    private ResourceLocation postEffectId;

    @Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
    public void checkEntityPostEffect(@Nullable Entity entity, CallbackInfo callback) {
        // vanilla has set no effect, implements the same behaviour as NeoForge
        if (this.postEffectId == null) {
            EntitySpectatorShadersContextFabricImpl.getEntityShader(entity).ifPresent(this::setPostEffect);
        }
    }

    @Shadow
    protected abstract void setPostEffect(ResourceLocation resourceLocation);

    @ModifyReturnValue(method = "getFov", at = @At("TAIL"))
    private float getFov(float fieldOfViewValue, Camera camera, float partialTicks, boolean useFOVSetting) {
        DefaultedFloat fieldOfView = DefaultedFloat.fromValue(fieldOfViewValue);
        FabricRendererEvents.COMPUTE_FIELD_OF_VIEW.invoker()
                .onComputeFieldOfView(GameRenderer.class.cast(this), this.mainCamera, partialTicks, fieldOfView);
        return fieldOfView.getAsOptionalFloat().orElse(fieldOfViewValue);
    }
}
