package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 800)
public abstract class GuiFabricMixin extends GuiComponent {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;III)V"))
    public void render(PoseStack poseStack, float partialTick, CallbackInfo callback) {
        DefaultedInt posX = DefaultedInt.fromValue(0);
        DefaultedInt posY = DefaultedInt.fromValue(this.screenHeight - 48);
        FabricClientEvents.CUSTOMIZE_CHAT_PANEL.invoker().onRenderChatPanel(this.minecraft.getWindow(), poseStack, partialTick, posX, posY);
        if (posX.getAsOptionalInt().isPresent() || posY.getAsOptionalInt().isPresent()) {
            poseStack.translate(posX.getAsInt(), posY.getAsInt() - (this.screenHeight - 48), 0.0);
        }
    }

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
