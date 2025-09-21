package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
abstract class ScreenFabricMixin extends AbstractContainerEventHandler {

    @Inject(
            method = "renderWithTooltip", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            shift = At.Shift.AFTER
    )
    )
    public void renderWithTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback) {
        if (AbstractContainerScreen.class.isInstance(this)) {
            FabricGuiEvents.CONTAINER_SCREEN_BACKGROUND.invoker()
                    .onDrawBackground(AbstractContainerScreen.class.cast(this), guiGraphics, mouseX, mouseY);
        }
    }
}
