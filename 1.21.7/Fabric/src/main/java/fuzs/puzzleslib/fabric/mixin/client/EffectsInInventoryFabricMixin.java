package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.impl.event.CopyOnWriteForwardingList;
import fuzs.puzzleslib.impl.event.data.DefaultedBoolean;
import fuzs.puzzleslib.impl.event.data.DefaultedInt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EffectsInInventory.class)
abstract class EffectsInInventoryFabricMixin {
    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;
    @Shadow
    @Nullable
    private MobEffectInstance hoveredEffect;

    @Inject(
            method = "renderEffects", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;renderBackgrounds(Lnet/minecraft/client/gui/GuiGraphics;IILjava/lang/Iterable;Z)V"
    ), cancellable = true
    )
    private void renderEffects$0(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo callback, @Local(ordinal = 2) int horizontalOffset, @Local(
            ordinal = 3
    ) int availableSpace, @Local(ordinal = 0) boolean fullSizedWidgets, @Share("smallWidgets") LocalRef<DefaultedBoolean> smallWidgetsRef, @Share(
            "horizontalOffset"
    ) LocalRef<DefaultedInt> horizontalOffsetRef) {
        smallWidgetsRef.set(DefaultedBoolean.fromValue(!fullSizedWidgets));
        horizontalOffsetRef.set(DefaultedInt.fromValue(horizontalOffset));
        EventResult result = FabricGuiEvents.INVENTORY_MOB_EFFECTS.invoker()
                .onInventoryMobEffects(this.screen, availableSpace, smallWidgetsRef.get(), horizontalOffsetRef.get());
        if (result.isInterrupt()) {
            callback.cancel();
        }
    }

    @ModifyVariable(
            method = "renderEffects", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;renderBackgrounds(Lnet/minecraft/client/gui/GuiGraphics;IILjava/lang/Iterable;Z)V"
    ), ordinal = 0
    )
    private boolean renderEffects$1(boolean fullSizedWidgets, @Share("smallWidgets") LocalRef<DefaultedBoolean> smallWidgetsRef) {
        return smallWidgetsRef.get().getAsOptionalBoolean().map(smallWidgets -> !smallWidgets).orElse(fullSizedWidgets);
    }

    @ModifyVariable(
            method = "renderEffects", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;renderBackgrounds(Lnet/minecraft/client/gui/GuiGraphics;IILjava/lang/Iterable;Z)V"
    ), ordinal = 2
    )
    private int renderEffects$2(int horizontalOffset, @Share(
            "horizontalOffset"
    ) LocalRef<DefaultedInt> horizontalOffsetRef) {
        return horizontalOffsetRef.get().getAsOptionalInt().orElse(horizontalOffset);
    }

    @ModifyVariable(method = "renderTooltip", at = @At("STORE"))
    public List<Component> renderTooltip(List<Component> tooltipLines) {
        CopyOnWriteForwardingList<Component> list = new CopyOnWriteForwardingList<>(tooltipLines);
        FabricGuiEvents.GATHER_EFFECT_SCREEN_TOOLTIP.invoker()
                .onGatherEffectScreenTooltip(this.screen, this.hoveredEffect, list);
        return list.delegate();
    }
}
