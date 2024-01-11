package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
abstract class PlayerRendererFabricMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererFabricMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setModelProperties(Lnet/minecraft/client/player/AbstractClientPlayer;)V", shift = At.Shift.AFTER), cancellable = true)
    public void render$0(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback) {
        EventResult result = FabricClientEvents.BEFORE_RENDER_PLAYER.invoker().onBeforeRenderPlayer(entity, PlayerRenderer.class.cast(this), partialTicks, matrixStack, buffer, packedLight);
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render$1(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback) {
        FabricClientEvents.AFTER_RENDER_PLAYER.invoker().onAfterRenderPlayer(entity, PlayerRenderer.class.cast(this), partialTicks, matrixStack, buffer, packedLight);
    }
}
