package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class EntityRendererFabricMixin<T extends Entity, S extends EntityRenderState> {

    @WrapWithCondition(method = "submit",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    protected boolean submit(EntityRenderer<T, S> entityRenderer, S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        return FabricRendererEvents.SUBMIT_NAME_TAG.invoker()
                .onSubmitNameTag(entityRenderer, state, poseStack, submitNodeCollector, camera)
                .isPass();
    }

    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
            at = @At("RETURN"))
    public void createRenderState(T entity, float partialTicks, CallbackInfoReturnable<S> callback) {
        FabricRendererEvents.EXTRACT_ENTITY_RENDER_STATE.invoker()
                .onExtractEntityRenderState(entity, callback.getReturnValue(), partialTicks);
    }
}
