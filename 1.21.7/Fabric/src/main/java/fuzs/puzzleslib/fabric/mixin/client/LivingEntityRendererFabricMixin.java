package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererFabricMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {

    protected LivingEntityRendererFabricMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render$0(S entityRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
        EventResult result = FabricRendererEvents.BEFORE_RENDER_LIVING.invoker().onBeforeRenderEntity(entityRenderState,
                LivingEntityRenderer.class.cast(this), Mth.frac(entityRenderState.ageInTicks), poseStack, bufferSource,
                packedLight
        );
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render$1(S entityRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
        FabricRendererEvents.AFTER_RENDER_LIVING.invoker().onAfterRenderEntity(entityRenderState,
                LivingEntityRenderer.class.cast(this), Mth.frac(entityRenderState.ageInTicks), poseStack, bufferSource,
                packedLight
        );
    }
}
