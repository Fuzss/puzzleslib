package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.impl.client.core.context.GuiLayersContextFabricImpl;
import fuzs.puzzleslib.fabric.impl.client.event.FabricGuiEventHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 900)
abstract class GuiFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private int tickCount;
    @Shadow
    public int displayHealth;
    @Shadow
    private long healthBlinkTime;

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

    @WrapMethod(method = "renderItemHotbar")
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.HOTBAR,
                guiGraphics,
                deltaTracker,
                () -> operation.call(guiGraphics, deltaTracker));
    }

    @Inject(method = "renderJumpMeter", at = @At(value = "HEAD"))
    private void renderJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.JUMP_METER, callback);
    }

    @WrapMethod(method = "renderJumpMeter")
    private void renderJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.JUMP_METER,
                guiGraphics,
                this.minecraft.getDeltaTracker(),
                () -> operation.call(rideable, guiGraphics, x));
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    public void renderExperienceBar(GuiGraphics guiGraphics, int xPos, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.EXPERIENCE_BAR, callback);
    }

    @WrapMethod(method = "renderExperienceBar")
    public void renderExperienceBar(GuiGraphics guiGraphics, int xPos, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.EXPERIENCE_BAR,
                guiGraphics,
                this.minecraft.getDeltaTracker(),
                () -> operation.call(guiGraphics, xPos));
    }

    @Inject(method = "renderExperienceLevel", at = @At(value = "HEAD"))
    private void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.EXPERIENCE_LEVEL, callback);
    }

    @Inject(
            method = "renderPlayerHealth", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderArmor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIII)V"
    )
    )
    private void renderPlayerHealth(GuiGraphics guiGraphics, CallbackInfo callback, @Share("heartsAlreadyRendered") LocalBooleanRef heartsAlreadyRendered) {
        if (GuiLayersContextFabricImpl.REPLACED_GUI_LAYERS.containsKey(GuiLayersContext.PLAYER_HEALTH)) {
            heartsAlreadyRendered.set(true);
            GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.PLAYER_HEALTH,
                    guiGraphics,
                    this.minecraft.getDeltaTracker(),
                    () -> {
                        Player player = this.getCameraPlayer();
                        int currentHealth = Mth.ceil(player.getHealth());
                        boolean renderHighlight = this.healthBlinkTime > this.tickCount &&
                                (this.healthBlinkTime - this.tickCount) / 3L % 2L == 1L;
                        int displayHealth = this.displayHealth;
                        int x = guiGraphics.guiWidth() / 2 - 91;
                        int y = guiGraphics.guiHeight() - 39;
                        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                                Math.max(displayHealth, currentHealth));
                        int absorptionAmount = Mth.ceil(player.getAbsorptionAmount());
                        int healthRows = Mth.ceil((maxHealth + absorptionAmount) / 2.0F / 10.0F);
                        int height = Math.max(10 - (healthRows - 2), 3);
                        int offsetHeartIndex = -1;
                        if (player.hasEffect(MobEffects.REGENERATION)) {
                            offsetHeartIndex = this.tickCount % Mth.ceil(maxHealth + 5.0F);
                        }
                        this.renderHearts(guiGraphics,
                                player,
                                x,
                                y,
                                height,
                                offsetHeartIndex,
                                maxHealth,
                                currentHealth,
                                displayHealth,
                                absorptionAmount,
                                renderHighlight);
                        GuiLayersContextFabricImpl.applyPlayerHealthGuiHeight(Gui.class.cast(this));
                    });
        }
    }

    @Shadow
    private Player getCameraPlayer() {
        throw new RuntimeException();
    }

    @Shadow
    private void renderHearts(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight) {
        throw new RuntimeException();
    }

    @WrapWithCondition(
            method = "renderPlayerHealth", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"
    )
    )
    private boolean renderPlayerHealth(Gui gui, GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, @Share(
            "heartsAlreadyRendered"
    ) LocalBooleanRef heartsAlreadyRendered) {
        if (!heartsAlreadyRendered.get()) {
            GuiLayersContextFabricImpl.applyPlayerHealthGuiHeight(gui);
            return true;
        } else {
            return false;
        }
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.ARMOR_LEVEL, callback);
    }

    @WrapMethod(method = "renderArmor")
    private static void renderArmor(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.ARMOR_LEVEL,
                guiGraphics,
                Minecraft.getInstance().getDeltaTracker(),
                () -> {
                    operation.call(guiGraphics, player, y, heartRows, height, x);
                    GuiLayersContextFabricImpl.applyArmorLevelGuiHeight(Minecraft.getInstance().gui);
                });
    }

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.PLAYER_HEALTH, callback);
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    public void renderFood(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.FOOD_LEVEL, callback);
    }

    @WrapMethod(method = "renderFood")
    public void renderFood(GuiGraphics guiGraphics, Player player, int y, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.FOOD_LEVEL,
                guiGraphics,
                this.minecraft.getDeltaTracker(),
                () -> {
                    operation.call(guiGraphics, player, y, x);
                    GuiLayersContextFabricImpl.applyFoodLevelGuiHeight(Gui.class.cast(this));
                });
    }

    @Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
    private void renderAirBubbles(GuiGraphics guiGraphics, Player player, int vehicleMaxHealth, int y, int x, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.AIR_LEVEL, callback);
    }

    @WrapMethod(method = "renderAirBubbles")
    private void renderAirBubbles(GuiGraphics guiGraphics, Player player, int vehicleMaxHealth, int y, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.AIR_LEVEL,
                guiGraphics,
                this.minecraft.getDeltaTracker(),
                () -> {
                    operation.call(guiGraphics, player, vehicleMaxHealth, y, x);
                    GuiLayersContextFabricImpl.applyAirLevelGuiHeight(Gui.class.cast(this));
                });
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    public void renderVehicleHealth(GuiGraphics guiGraphics, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.VEHICLE_HEALTH, callback);
    }

    @WrapMethod(method = "renderVehicleHealth")
    public void renderVehicleHealth(GuiGraphics guiGraphics, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.VEHICLE_HEALTH,
                guiGraphics,
                this.minecraft.getDeltaTracker(),
                () -> {
                    operation.call(guiGraphics);
                    GuiLayersContextFabricImpl.applyVehicleHealthGuiHeight(Gui.class.cast(this));
                });
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.SELECTED_ITEM_NAME, callback);
    }

    @WrapMethod(method = "renderSelectedItemName")
    public void renderSelectedItemName(GuiGraphics guiGraphics, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.SELECTED_ITEM_NAME,
                guiGraphics,
                this.minecraft.getDeltaTracker(),
                () -> operation.call(guiGraphics));
    }

    @ModifyExpressionValue(
            method = "renderSelectedItemName",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;guiHeight()I")
    )
    public int renderSelectedItemName(int guiHeight) {
        // offset selected item name depending on gui height values, NeoForge also does this
        return guiHeight + 59 - Math.max(59,
                Math.max(GuiHeightHelper.getLeftHeight(Gui.class.cast(this)),
                        GuiHeightHelper.getRightHeight(Gui.class.cast(this))));
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.STATUS_EFFECTS, callback);
    }

    @Inject(method = "renderSavingIndicator", at = @At("HEAD"), cancellable = true)
    public void renderSavingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        FabricGuiEventHelper.cancelIfNecessary(RenderGuiLayerEvents.SAVING_INDICATOR, callback);
    }
}
