package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
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
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.CAMERA_OVERLAYS)
                .invoker()
                .onBeforeRenderGuiLayer(this.minecraft, guiGraphics, deltaTracker)
                .isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderCameraOverlays", at = @At("TAIL"))
    private void renderCameraOverlays$1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.CAMERA_OVERLAYS).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker);
    }

    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    private void renderItemHotbar$0(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.HOTBAR).invoker().onBeforeRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    private void renderItemHotbar$1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.HOTBAR).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker);
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair$0(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.CROSSHAIR).invoker().onBeforeRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderCrosshair$1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.CROSSHAIR).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker);
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects$0(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.EFFECTS).invoker().onBeforeRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderEffects", at = @At("TAIL"))
    protected void renderEffects$1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.EFFECTS).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, deltaTracker);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    public void renderExperienceBar$0(GuiGraphics guiGraphics, int xPos, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.EXPERIENCE_BAR)
                .invoker()
                .onBeforeRenderGuiLayer(this.minecraft, guiGraphics, this.minecraft.getTimer())
                .isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "TAIL"))
    public void renderExperienceBar$1(GuiGraphics guiGraphics, int xPos, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.EXPERIENCE_BAR).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer());
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName$0(GuiGraphics guiGraphics, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.SELECTED_ITEM_NAME)
                .invoker()
                .onBeforeRenderGuiLayer(this.minecraft, guiGraphics, this.minecraft.getTimer())
                .isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderSelectedItemName", at = @At("TAIL"))
    public void renderSelectedItemName$1(GuiGraphics guiGraphics, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.SELECTED_ITEM_NAME).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer());
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor$0(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo callback) {
        Minecraft minecraft = Minecraft.getInstance();
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.ARMOR_LEVEL).invoker().onBeforeRenderGuiLayer(
                minecraft, guiGraphics, minecraft.getTimer()).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderArmor", at = @At("TAIL"))
    private static void renderArmor$1(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo callback) {
        Minecraft minecraft = Minecraft.getInstance();
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.ARMOR_LEVEL).invoker().onAfterRenderGuiLayer(
                minecraft, guiGraphics, minecraft.getTimer());
    }

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts$0(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.PLAYER_HEALTH).invoker().onBeforeRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer()).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderHearts", at = @At("HEAD"))
    private void renderHearts$1(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.PLAYER_HEALTH).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer());
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    public void renderFood$0(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.FOOD_LEVEL).invoker().onBeforeRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer()).isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderFood", at = @At("TAIL"))
    public void renderFood$1(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.FOOD_LEVEL).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer());
    }

    @ModifyExpressionValue(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAirSupply()I")
    )
    private int renderPlayerHealth(int airSupply, GuiGraphics guiGraphics) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.AIR_LEVEL).invoker().onBeforeRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer()).isInterrupt()) {
            return 0;
        } else {
            return airSupply;
        }
    }

    @Inject(method = "renderPlayerHealth", at = @At("TAIL"))
    private void renderPlayerHealth(GuiGraphics guiGraphics, CallbackInfo callback) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.AIR_LEVEL).invoker().onAfterRenderGuiLayer(
                    this.minecraft, guiGraphics, this.minecraft.getTimer());
        }
    }

    @Shadow
    @Nullable
    private Player getCameraPlayer() {
        throw new RuntimeException();
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    public void renderVehicleHealth$0(GuiGraphics guiGraphics, CallbackInfo callback) {
        if (FabricGuiEvents.beforeRenderGuiElement(RenderGuiLayerEvents.VEHICLE_HEALTH)
                .invoker()
                .onBeforeRenderGuiLayer(this.minecraft, guiGraphics, this.minecraft.getTimer())
                .isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "renderVehicleHealth", at = @At("TAIL"))
    public void renderVehicleHealth$1(GuiGraphics guiGraphics, CallbackInfo callback) {
        FabricGuiEvents.afterRenderGuiElement(RenderGuiLayerEvents.VEHICLE_HEALTH).invoker().onAfterRenderGuiLayer(
                this.minecraft, guiGraphics, this.minecraft.getTimer());
    }
}
