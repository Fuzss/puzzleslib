package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.impl.client.event.FabricGuiEventHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 500)
abstract class GuiFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/LayeredDraw;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
    )
    )
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEvents.BEFORE_RENDER_GUI.invoker().onBeforeRenderGui(Gui.class.cast(this), guiGraphics, deltaTracker);
    }

    @WrapOperation(
            method = "renderChat", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V"
    )
    )
    private void renderChat(ChatComponent chatComponent, GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, Operation<Void> operation, GuiGraphics $, DeltaTracker deltaTracker) {
        guiGraphics.pose().pushPose();
        DefaultedInt posX = DefaultedInt.fromValue(0);
        DefaultedInt posY = DefaultedInt.fromValue(guiGraphics.guiHeight() - 48);
        FabricGuiEvents.CUSTOMIZE_CHAT_PANEL.invoker().onRenderChatPanel(guiGraphics, deltaTracker, posX, posY);
        if (posX.getAsOptionalInt().isPresent() || posY.getAsOptionalInt().isPresent()) {
            guiGraphics.pose().translate(posX.getAsInt(), posY.getAsInt() - (guiGraphics.guiHeight() - 48), 0.0);
        }
        operation.call(chatComponent, guiGraphics, tickCount, mouseX, mouseY, focused);
        guiGraphics.pose().popPose();
    }

    @Inject(method = "renderCameraOverlays", at = @At("HEAD"), cancellable = true)
    private void renderCameraOverlays$0(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.CAMERA_OVERLAYS, callback);
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.CROSSHAIR, callback);
    }

    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.HOTBAR, callback);
    }

    @Inject(method = "renderJumpMeter", at = @At(value = "HEAD"))
    private void renderJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.JUMP_METER, callback);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    public void renderExperienceBar(GuiGraphics guiGraphics, int xPos, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.EXPERIENCE_BAR, callback);
    }

    @Inject(method = "renderExperienceLevel", at = @At(value = "HEAD"))
    private void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.EXPERIENCE_LEVEL, callback);
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.ARMOR_LEVEL, callback);
    }

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.PLAYER_HEALTH, callback);
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    public void renderFood(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.FOOD_LEVEL, callback);
    }

    @Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
    private void renderAirBubbles(GuiGraphics guiGraphics, Player player, int i, int j, int k, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.AIR_LEVEL, callback);
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    public void renderVehicleHealth(GuiGraphics guiGraphics, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.VEHICLE_HEALTH, callback);
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.SELECTED_ITEM_NAME, callback);
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.EFFECTS, callback);
    }

    @Inject(method = "renderSavingIndicator", at = @At("HEAD"), cancellable = true)
    public void renderSavingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.SAVING_INDICATOR, callback);
    }
}
