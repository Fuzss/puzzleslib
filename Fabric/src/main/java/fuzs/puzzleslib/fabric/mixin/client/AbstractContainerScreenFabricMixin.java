package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    @Inject(method = "extractContents",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
                     shift = At.Shift.AFTER))
    public void renderContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        FabricGuiEvents.RENDER_CONTAINER_SCREEN_CONTENTS.invoker()
                .onRenderContainerScreenContents(AbstractContainerScreen.class.cast(this), guiGraphics, mouseX, mouseY);
    }
}
