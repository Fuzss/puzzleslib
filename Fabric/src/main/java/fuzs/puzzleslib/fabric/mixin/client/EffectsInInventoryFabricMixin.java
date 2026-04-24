package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.common.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.common.impl.event.CopyOnWriteForwardingList;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import org.spongepowered.asm.mixin.injection.Slice;
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

    @WrapOperation(method = "extractRenderState",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;extractEffects(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/Collection;IIIII)V"))
    public void render(EffectsInInventory effectsInInventory, GuiGraphicsExtractor graphics, Collection<MobEffectInstance> activeEffects, int posX, int posY, int mouseX, int mouseY, int maxWidth, Operation<Void> operation) {
        int availableWidth = this.screen.width - posX;
        boolean smallWidgets = maxWidth == 32;
        MutableBoolean smallWidgetsValue = MutableBoolean.fromValue(smallWidgets);
        MutableInt horizontalPositionValue = MutableInt.fromValue(posX);
        EventResult eventResult = FabricGuiEvents.INVENTORY_MOB_EFFECTS.invoker()
                .onPrepareInventoryMobEffects(this.screen, availableWidth, smallWidgetsValue, horizontalPositionValue);
        if (smallWidgetsValue.getAsBoolean() != smallWidgets) {
            // Careful, this is inverted compared to vanilla.
            maxWidth = smallWidgetsValue.getAsBoolean() ? 32 : availableWidth - 7;
        }

        if (eventResult.isPass()) {
            operation.call(effectsInInventory,
                    graphics,
                    activeEffects,
                    horizontalPositionValue.getAsInt(),
                    posY,
                    mouseX,
                    mouseY,
                    maxWidth);
        }
    }

    @ModifyVariable(method = "extractEffects", at = @At("STORE"))
    private MobEffectInstance renderEffects(MobEffectInstance effect) {
        this.puzzleslib$hoveredEffect = effect;
        return effect;
    }

    @ModifyVariable(method = "extractText",
                    at = @At("STORE"),
                    ordinal = 0,
                    slice = @Slice(from = @At(value = "INVOKE",
                                              target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;clipText(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;I)Lnet/minecraft/util/FormattedCharSequence;")))
    private boolean renderText(boolean isCompact, @Share("wasCompact") LocalBooleanRef wasCompact) {
        if (this.puzzleslib$hoveredEffect != null) {
            wasCompact.set(isCompact);
            // This makes the tooltip always render, so we can then modify it later on.
            return true;
        } else {
            return isCompact;
        }
    }

    @ModifyArg(method = "extractText",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    private List<Component> renderText(List<Component> tooltipLines, @Cancellable CallbackInfo callback, @Share(
            "wasCompact") LocalBooleanRef wasCompact) {
        if (this.puzzleslib$hoveredEffect != null) {
            CopyOnWriteForwardingList<Component> wrappedTooltipLines = new CopyOnWriteForwardingList<>(
                    wasCompact.get() ? tooltipLines : Collections.emptyList());
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
