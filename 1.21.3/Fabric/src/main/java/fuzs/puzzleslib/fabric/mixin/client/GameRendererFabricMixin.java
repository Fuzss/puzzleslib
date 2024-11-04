package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
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

    @ModifyReturnValue(method = "getFov", at = @At("TAIL"))
    private double getFov(double fieldOfViewValue, Camera camera, float partialTicks, boolean useFOVSetting) {
        DefaultedDouble fieldOfView = DefaultedDouble.fromValue(fieldOfViewValue);
        FabricRendererEvents.COMPUTE_FIELD_OF_VIEW.invoker().onComputeFieldOfView(GameRenderer.class.cast(this), this.mainCamera, partialTicks, fieldOfView);
        return fieldOfView.getAsOptionalDouble().orElse(fieldOfViewValue);
    }
}
