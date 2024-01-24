package fuzs.puzzleslib.forge.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.forge.impl.client.event.ForgeMouseDraggedEvents;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
abstract class MouseHandlerForgeMixin {
    @Shadow
    private int activeButton;

    @SuppressWarnings("target")
    @Inject(method = "method_1602(Lnet/minecraft/client/gui/screens/Screen;DDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(DDIDD)Z"), cancellable = true)
    private void onMove$0(Screen screen, double mouseX, double mouseY, double dragX, double dragY, CallbackInfo callback) {
        if (MinecraftForge.EVENT_BUS.post(new ForgeMouseDraggedEvents.Pre(screen, mouseX, mouseY, this.activeButton, dragX, dragY))) {
            callback.cancel();
        }
    }

    @SuppressWarnings("target")
    @WrapOperation(method = "method_1602(Lnet/minecraft/client/gui/screens/Screen;DDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(DDIDD)Z"))
    private boolean onMove$1(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY, Operation<Boolean> operation) {
        boolean result = operation.call(screen, mouseX, mouseY, button, dragX, dragY);
        if (!result) {
            MinecraftForge.EVENT_BUS.post(new ForgeMouseDraggedEvents.Post(screen, mouseX, mouseY, this.activeButton, dragX, dragY));
        }
        return result;
    }
}
