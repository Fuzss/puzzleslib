package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
abstract class PlayerRendererFabricMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    public PlayerRendererFabricMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render$0(PlayerRenderState playerRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
        EventResult result = FabricRendererEvents.BEFORE_RENDER_PLAYER.invoker().onBeforeRenderPlayer(playerRenderState,
                PlayerRenderer.class.cast(this), Mth.frac(playerRenderState.ageInTicks), poseStack, bufferSource,
                packedLight
        );
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render$1(PlayerRenderState playerRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
        FabricRendererEvents.AFTER_RENDER_PLAYER.invoker().onAfterRenderPlayer(playerRenderState,
                PlayerRenderer.class.cast(this), Mth.frac(playerRenderState.ageInTicks), poseStack, bufferSource,
                packedLight
        );
    }
}
