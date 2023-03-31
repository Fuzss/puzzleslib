package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 800)
public abstract class GuiFabricMixin extends GuiComponent {
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects$0(PoseStack poseStack, CallbackInfo callback) {
        if (FabricClientEvents.beforeRenderGuiElement(RenderGuiElementEvents.POTION_ICONS).invoker().onBeforeRenderGuiElement(poseStack, this.screenWidth, this.screenHeight).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderEffects", at = @At("TAIL"))
    protected void renderEffects$1(PoseStack poseStack, CallbackInfo callback) {
        FabricClientEvents.afterRenderGuiElement(RenderGuiElementEvents.POTION_ICONS).invoker().onAfterRenderGuiElement(poseStack, this.screenWidth, this.screenHeight);
    }
}
