package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import fuzs.puzzleslib.fabric.impl.client.util.EntityRenderStateExtension;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class EntityRendererFabricMixin<T extends Entity, S extends EntityRenderState> {

    @WrapWithCondition(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
    )
    )
    public boolean render(EntityRenderer<T, S> entityRenderer, S entityRenderState, Component content, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        return FabricRendererEvents.RENDER_NAME_TAG.invoker()
                .onRenderNameTag(entityRenderState, content, entityRenderer, poseStack, bufferSource, packedLight)
                .isPass();
    }

    @Inject(
            method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
            at = @At("TAIL")
    )
    public void createRenderState(T entity, float partialTick, CallbackInfoReturnable<S> callback) {
        S renderState = callback.getReturnValue();
        ((EntityRenderStateExtension) renderState).puzzleslib$clearRenderProperties();
        FabricRendererEvents.EXTRACT_RENDER_STATE.invoker().onExtractRenderState(entity, renderState, partialTick);
    }
}
