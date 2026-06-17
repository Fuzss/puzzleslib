package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererFabricMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void submitArmWithItem(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo callback) {
        if (hand == InteractionHand.MAIN_HAND) {
            EventResult result = FabricRendererEvents.RENDER_MAIN_HAND.invoker()
                    .onRenderMainHand(ItemInHandRenderer.class.cast(this),
                            hand,
                            player,
                            player.getMainArm(),
                            itemStack,
                            poseStack,
                            submitNodeCollector,
                            lightCoords,
                            frameInterp,
                            xRot,
                            attack,
                            inverseArmHeight);
            if (result.isInterrupt()) {
                callback.cancel();
            }
        } else if (hand == InteractionHand.OFF_HAND) {
            EventResult result = FabricRendererEvents.RENDER_OFF_HAND.invoker()
                    .onRenderOffHand(ItemInHandRenderer.class.cast(this),
                            hand,
                            player,
                            player.getMainArm().getOpposite(),
                            itemStack,
                            poseStack,
                            submitNodeCollector,
                            lightCoords,
                            frameInterp,
                            xRot,
                            attack,
                            inverseArmHeight);
            if (result.isInterrupt()) {
                callback.cancel();
            }
        }
    }
}
