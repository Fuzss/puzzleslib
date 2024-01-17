package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenFabricMixin extends Screen {

    protected AbstractContainerScreenFabricMixin(Component component) {
        super(component);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    public void render$0(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        FabricGuiEvents.CONTAINER_SCREEN_BACKGROUND.invoker().onDrawBackground(AbstractContainerScreen.class.cast(this), guiGraphics, mouseX, mouseY);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", shift = At.Shift.AFTER))
    public void render$1(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        FabricGuiEvents.CONTAINER_SCREEN_FOREGROUND.invoker().onDrawForeground(AbstractContainerScreen.class.cast(this), guiGraphics, mouseX, mouseY);
    }
}
