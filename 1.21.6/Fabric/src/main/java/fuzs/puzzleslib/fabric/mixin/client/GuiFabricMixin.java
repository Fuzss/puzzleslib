package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.fabric.impl.client.core.context.GuiLayersContextFabricImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
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

@Mixin(Gui.class)
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
            method = "renderHotbarAndDecorations", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
    )
    )
    private void renderHotbarAndDecorations(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.HOTBAR,
                guiGraphics,
                deltaTracker,
                () -> operation.call(gui, guiGraphics, deltaTracker));
    }

    @WrapOperation(
            method = "renderHotbarAndDecorations", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderJumpMeter(Lnet/minecraft/world/entity/PlayerRideableJumping;Lnet/minecraft/client/gui/GuiGraphics;I)V"
    )
    )
    private void renderHotbarAndDecorations(Gui gui, PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.JUMP_METER,
                guiGraphics,
                gui.minecraft.getDeltaTracker(),
                () -> operation.call(gui, rideable, guiGraphics, x));
    }

    @WrapOperation(
            method = "renderHotbarAndDecorations", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V"
    )
    )
    private void renderHotbarAndDecorations(Gui gui, GuiGraphics guiGraphics, int xPos, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.INFO_BAR,
                guiGraphics,
                gui.minecraft.getDeltaTracker(),
                () -> operation.call(gui, guiGraphics, xPos));
    }

    @Inject(
            method = "renderHotbarAndDecorations", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"
    )
    )
    private void renderHotbarAndDecorations(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback, @Share(
            "heartsAlreadyRendered"
    ) LocalBooleanRef heartsAlreadyRendered) {
        if (GuiLayersContextFabricImpl.REPLACED_GUI_LAYERS.containsKey(GuiLayersContext.VEHICLE_HEALTH)) {
            heartsAlreadyRendered.set(true);
            GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.VEHICLE_HEALTH,
                    guiGraphics,
                    deltaTracker,
                    () -> {
                        this.renderVehicleHealth(guiGraphics);
                        GuiLayersContextFabricImpl.applyVehicleHealthGuiHeight(Gui.class.cast(this));
                    });
        }
    }

    @Shadow
    private void renderVehicleHealth(GuiGraphics guiGraphics) {
        throw new RuntimeException();
    }

    @WrapWithCondition(
            method = "renderHotbarAndDecorations", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"
    )
    )
    private boolean renderHotbarAndDecorations(Gui gui, GuiGraphics guiGraphics, @Share(
            "heartsAlreadyRendered"
    ) LocalBooleanRef heartsAlreadyRendered) {
        if (!heartsAlreadyRendered.get()) {
            GuiLayersContextFabricImpl.applyVehicleHealthGuiHeight(gui);
            return true;
        } else {
            return false;
        }
    }

    @WrapOperation(
            method = "renderHotbarAndDecorations", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"
    )
    )
    private void renderHotbarAndDecorations(Gui gui, GuiGraphics guiGraphics, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.SELECTED_ITEM_NAME,
                guiGraphics,
                gui.minecraft.getDeltaTracker(),
                () -> operation.call(gui, guiGraphics));
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

    @WrapOperation(
            method = "renderPlayerHealth", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderArmor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIII)V"
    )
    )
    private void renderPlayerHealth(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, Operation<Void> operation) {
        Gui gui = Gui.class.cast(this);
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.ARMOR_LEVEL,
                guiGraphics,
                gui.minecraft.getDeltaTracker(),
                () -> {
                    operation.call(guiGraphics, player, y, heartRows, height, x);
                    GuiLayersContextFabricImpl.applyArmorLevelGuiHeight(gui);
                });
    }

    @WrapOperation(
            method = "renderPlayerHealth", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V"
    )
    )
    private void renderPlayerHealth(Gui gui, GuiGraphics guiGraphics, Player player, int y, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.FOOD_LEVEL,
                guiGraphics,
                gui.minecraft.getDeltaTracker(),
                () -> {
                    operation.call(gui, guiGraphics, player, y, x);
                    GuiLayersContextFabricImpl.applyFoodLevelGuiHeight(gui);
                });
    }

    @WrapOperation(
            method = "renderPlayerHealth", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderAirBubbles(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;III)V"
    )
    )
    private void renderPlayerHealth(Gui gui, GuiGraphics guiGraphics, Player player, int vehicleMaxHealth, int y, int x, Operation<Void> operation) {
        GuiLayersContextFabricImpl.renderGuiLayer(GuiLayersContext.AIR_LEVEL,
                guiGraphics,
                gui.minecraft.getDeltaTracker(),
                () -> {
                    operation.call(gui, guiGraphics, player, vehicleMaxHealth, y, x);
                    GuiLayersContextFabricImpl.applyAirLevelGuiHeight(gui);
                });
    }
}
