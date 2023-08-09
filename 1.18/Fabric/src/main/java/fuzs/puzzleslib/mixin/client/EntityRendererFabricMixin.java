package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
abstract class EntityRendererFabricMixin<T extends Entity> {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback) {
        DefaultedValue<Component> content = DefaultedValue.fromValue(entity.getDisplayName());
        EventResult result = FabricClientEvents.RENDER_NAME_TAG.invoker().onRenderNameTag(entity, content, EntityRenderer.class.cast(this), poseStack, buffer, packedLight, partialTick);
        if (result.isInterrupt()) {
            callback.cancel();
            if (!result.getAsBoolean()) return;
        } else if (content.getAsOptional().isEmpty()) {
            return;
        } else {
            callback.cancel();
            if (!this.shouldShowName(entity)) return;
        }
        this.renderNameTag(entity, content.get(), poseStack, buffer, packedLight);
    }

    @Shadow
    protected abstract boolean shouldShowName(T entity);

    @Shadow
    protected abstract void renderNameTag(T entity, Component displayName, PoseStack matrixStack, MultiBufferSource buffer, int packedLight);
}
