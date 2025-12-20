package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.impl.event.CopyOnWriteForwardingList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(EffectsInInventory.class)
abstract class EffectsInInventoryFabricMixin {
    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;
    @Unique
    @Nullable
    private MobEffectInstance puzzleslib$hoveredEffect;

    @WrapOperation(method = "render",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/Collection;IIIII)V"))
    public void render(EffectsInInventory effectsInInventory, GuiGraphics guiGraphics, Collection<MobEffectInstance> collection, int posX, int posY, int mouseX, int mouseY, int maxWidgetWidth, Operation<Void> operation) {
        int maxWidth = this.screen.width - posX;
        boolean smallWidgets = maxWidgetWidth == 32;
        MutableBoolean smallWidgetsValue = MutableBoolean.fromValue(smallWidgets);
        MutableInt horizontalPositionValue = MutableInt.fromValue(posX);
        EventResult eventResult = FabricGuiEvents.INVENTORY_MOB_EFFECTS.invoker()
                .onPrepareInventoryMobEffects(this.screen, maxWidth, smallWidgetsValue, horizontalPositionValue);
        if (smallWidgetsValue.getAsBoolean() != smallWidgets) {
            // Careful, this is inverted compared to vanilla.
            maxWidgetWidth = smallWidgetsValue.getAsBoolean() ? 32 : maxWidth - 7;
        }

        if (eventResult.isPass()) {
            operation.call(effectsInInventory,
                    guiGraphics,
                    collection,
                    horizontalPositionValue.getAsInt(),
                    posY,
                    mouseX,
                    mouseY,
                    maxWidgetWidth);
        }
    }

    @ModifyVariable(method = "renderEffects", at = @At("STORE"))
    private MobEffectInstance renderEffects(MobEffectInstance mobEffect) {
        this.puzzleslib$hoveredEffect = mobEffect;
        return mobEffect;
    }

    @ModifyVariable(method = "renderText", at = @At(value = "LOAD", ordinal = 2))
    private boolean renderText(boolean mustClipText, @Share("mustClipText") LocalBooleanRef originalMustClipText) {
        if (this.puzzleslib$hoveredEffect != null) {
            originalMustClipText.set(mustClipText);
            // This makes the tooltip always render, so we can then modify it later on.
            return true;
        } else {
            return mustClipText;
        }
    }

    @ModifyArg(method = "renderText",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    private List<Component> renderText(List<Component> tooltipLines, @Cancellable CallbackInfo callback, @Share(
            "mustClipText") LocalBooleanRef originalMustClipText) {
        if (this.puzzleslib$hoveredEffect != null) {
            CopyOnWriteForwardingList<Component> wrappedTooltipLines = new CopyOnWriteForwardingList<>(
                    originalMustClipText.get() ? tooltipLines : Collections.emptyList());
            FabricGuiEvents.GATHER_EFFECT_SCREEN_TOOLTIP.invoker()
                    .onGatherEffectScreenTooltip(this.screen, this.puzzleslib$hoveredEffect, wrappedTooltipLines);
            if (wrappedTooltipLines.isEmpty()) {
                callback.cancel();
            }

            return wrappedTooltipLines.delegate();
        } else {
            return tooltipLines;
        }
    }
}
