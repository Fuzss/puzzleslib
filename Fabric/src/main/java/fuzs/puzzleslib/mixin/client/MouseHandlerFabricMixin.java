package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.ExtraScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// run before Mouse Tweaks mod
@Mixin(value = MouseHandler.class, priority = 900)
abstract class MouseHandlerFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private int activeButton;
    @Unique
    private Screen puzzleslib$currentScreen;

    @SuppressWarnings("target")
    @Inject(method = "method_1602(Lnet/minecraft/client/gui/screens/Screen;DDDD)V", at = @At("HEAD"), cancellable = true)
    private void onMove$0(Screen screen, double mouseX, double mouseY, double dragX, double dragY, CallbackInfo callback) {

        // Store the screen in a variable in case someone tries to change the screen during this before event.
        // If someone changes the screen, the after event will likely have class cast exceptions or throw a NPE.
        this.puzzleslib$currentScreen = this.minecraft.screen;

        if (this.puzzleslib$currentScreen == null) return;

        if (!ExtraScreenMouseEvents.allowMouseDrag(this.puzzleslib$currentScreen).invoker().allowMouseDrag(this.puzzleslib$currentScreen, mouseX, mouseY, this.activeButton, dragX, dragY)) {
            this.puzzleslib$currentScreen = null;
            callback.cancel();
            return;
        }

        ExtraScreenMouseEvents.beforeMouseDrag(this.puzzleslib$currentScreen).invoker().beforeMouseDrag(this.puzzleslib$currentScreen, mouseX, mouseY, this.activeButton, dragX, dragY);
    }

    @SuppressWarnings("target")
    @Inject(method = "method_1602(Lnet/minecraft/client/gui/screens/Screen;DDDD)V", at = @At("TAIL"))
    private void onMove$1(Screen screen, double mouseX, double mouseY, double dragX, double dragY, CallbackInfo callback) {

        if (this.puzzleslib$currentScreen == null) return;

        // On Forge this only runs when Screen::mouseDragged returns false, but vanilla does not capture the result from that method invocation.
        // We can't just call the method ourselves, as that would require replacing the vanilla invocation, which messed with other mods placing their own hook here (namely Mouse Tweaks),
        // so there is no way of knowing if vanilla was successful on Fabric right now.
        ExtraScreenMouseEvents.afterMouseDrag(this.puzzleslib$currentScreen).invoker().afterMouseDrag(this.puzzleslib$currentScreen, mouseX, mouseY, this.activeButton, dragX, dragY);
        this.puzzleslib$currentScreen = null;
    }
}
