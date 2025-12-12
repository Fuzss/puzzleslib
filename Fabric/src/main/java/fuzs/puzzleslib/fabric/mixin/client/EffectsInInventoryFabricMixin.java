package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(EffectsInInventory.class)
abstract class EffectsInInventoryFabricMixin {
    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;

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
            maxWidgetWidth = smallWidgetsValue.getAsBoolean() ? maxWidth - 7 : 32;
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
}
