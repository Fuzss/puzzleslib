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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 500)
abstract class GuiFabricMixin extends GuiComponent {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;
    @Unique
    private float puzzleslib$partialTick;

    @Inject(method = "render", at = @At("HEAD"))
    public void render$0(PoseStack poseStack, float partialTick, CallbackInfo callback) {
        this.puzzleslib$partialTick = partialTick;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", shift = At.Shift.BEFORE))
    public void render$1(PoseStack poseStack, float partialTick, CallbackInfo callback) {
        poseStack.pushPose();
        DefaultedInt posX = DefaultedInt.fromValue(0);
        DefaultedInt posY = DefaultedInt.fromValue(this.screenHeight - 48);
        FabricClientEvents.CUSTOMIZE_CHAT_PANEL.invoker().onRenderChatPanel(this.minecraft.getWindow(), poseStack, partialTick, posX, posY);
        if (posX.getAsOptionalInt().isPresent() || posY.getAsOptionalInt().isPresent()) {
            poseStack.translate(posX.getAsInt(), posY.getAsInt() - (this.screenHeight - 48), 0.0);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", shift = At.Shift.AFTER))
    public void render$2(PoseStack poseStack, float partialTick, CallbackInfo callback) {
        poseStack.popPose();
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects$0(PoseStack poseStack, CallbackInfo callback) {
        if (FabricClientEvents.beforeRenderGuiElement(RenderGuiElementEvents.POTION_ICONS.id()).invoker().onBeforeRenderGuiElement(this.minecraft, poseStack, this.puzzleslib$partialTick, this.screenWidth, this.screenHeight).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderEffects", at = @At("TAIL"))
    protected void renderEffects$1(PoseStack poseStack, CallbackInfo callback) {
        FabricClientEvents.afterRenderGuiElement(RenderGuiElementEvents.POTION_ICONS.id()).invoker().onAfterRenderGuiElement(this.minecraft, poseStack, this.puzzleslib$partialTick, this.screenWidth, this.screenHeight);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    public void renderExperienceBar$0(PoseStack poseStack, int xPos, CallbackInfo callback) {
        if (FabricClientEvents.beforeRenderGuiElement(RenderGuiElementEvents.EXPERIENCE_BAR.id()).invoker().onBeforeRenderGuiElement(this.minecraft, poseStack, this.puzzleslib$partialTick, this.screenWidth, this.screenHeight).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "TAIL"))
    public void renderExperienceBar$1(PoseStack poseStack, int xPos, CallbackInfo callback) {
        FabricClientEvents.afterRenderGuiElement(RenderGuiElementEvents.EXPERIENCE_BAR.id()).invoker().onAfterRenderGuiElement(this.minecraft, poseStack, this.puzzleslib$partialTick, this.screenWidth, this.screenHeight);
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName$0(PoseStack poseStack, CallbackInfo callback) {
        if (FabricClientEvents.beforeRenderGuiElement(RenderGuiElementEvents.ITEM_NAME.id()).invoker().onBeforeRenderGuiElement(this.minecraft, poseStack, this.puzzleslib$partialTick, this.screenWidth, this.screenHeight).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderSelectedItemName", at = @At("TAIL"))
    public void renderSelectedItemName$1(PoseStack poseStack, CallbackInfo callback) {
        FabricClientEvents.afterRenderGuiElement(RenderGuiElementEvents.ITEM_NAME.id()).invoker().onAfterRenderGuiElement(this.minecraft, poseStack, this.puzzleslib$partialTick, this.screenWidth, this.screenHeight);
    }
}
