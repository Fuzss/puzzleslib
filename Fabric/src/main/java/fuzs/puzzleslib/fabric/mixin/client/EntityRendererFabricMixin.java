package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
abstract class EntityRendererFabricMixin<T extends Entity, S extends EntityRenderState> {

    @WrapWithCondition(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
    )
    )
    public boolean render(EntityRenderer<T, S> entityRenderer, S entityRenderState, Component content, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        return FabricRendererEvents.RENDER_NAME_TAG.invoker().onRenderNameTag(entityRenderState, content,
                entityRenderer, poseStack, bufferSource, packedLight, Mth.frac(entityRenderState.ageInTicks)
        ).isPass();
    }

    @ModifyVariable(method = "extractRenderState", at = @At("STORE"))
    public boolean extractRenderState(boolean renderNameTag, T entity, S entityRenderState, float partialTick) {
        EventResult result = FabricRendererEvents.ALLOW_NAME_TAG.invoker().onAllowNameTag(entity, entityRenderState,
                this.getNameTag(entity), EntityRenderer.class.cast(this), partialTick
        );
        return result.isInterrupt() ? result.getAsBoolean() : renderNameTag;
    }

    @Shadow
    protected abstract Component getNameTag(T arg);
}
