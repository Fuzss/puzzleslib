package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.ExtraScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
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

import java.util.Objects;

// run before Mouse Tweaks mod
@Mixin(value = MouseHandler.class, priority = 500)
abstract class MouseHandlerFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private double xpos;
    @Shadow
    private double ypos;
    @Shadow
    private int activeButton;
    @Unique
    private Screen puzzleslib$currentScreen;
    @Unique
    private Double puzzleslib$verticalScrollAmount;
    @Unique
    private Double puzzleslib$horizontalScrollAmount;

    @Inject(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", ordinal = 0), cancellable = true)
    private void onPress$0(long windowPointer, int button, int action, int modifiers, CallbackInfo callback) {
        EventResult result = FabricClientEvents.BEFORE_MOUSE_ACTION.invoker().onBeforeMouseAction(button, action, modifiers);
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "onPress", at = @At("TAIL"))
    private void onPress$1(long windowPointer, int button, int action, int modifiers, CallbackInfo callback) {
        if (windowPointer == Minecraft.getInstance().getWindow().getWindow()) {
            FabricClientEvents.AFTER_MOUSE_ACTION.invoker().onAfterMouseAction(button, action, modifiers);
        }
    }

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    private void onScroll$0(long windowPointer, double xOffset, double yOffset, CallbackInfo callback) {
        // just recalculate this instead of capturing local, shouldn't be able to change in the meantime
        this.puzzleslib$verticalScrollAmount = this.minecraft.options.discreteMouseScroll().get() ? Math.signum(yOffset) : yOffset * this.minecraft.options.mouseWheelSensitivity().get();
        // apply same calculations to horizontal scroll as vertical scroll amount has
        this.puzzleslib$horizontalScrollAmount = this.minecraft.options.discreteMouseScroll().get() ? Math.signum(xOffset) : xOffset * this.minecraft.options.mouseWheelSensitivity().get();
        if (FabricClientEvents.BEFORE_MOUSE_SCROLL.invoker().onBeforeMouseScroll(this.isLeftPressed(), this.isMiddlePressed(), this.isRightPressed(), this.puzzleslib$horizontalScrollAmount, this.puzzleslib$verticalScrollAmount).isInterrupt()) {
            this.puzzleslib$verticalScrollAmount = null;
            this.puzzleslib$horizontalScrollAmount = null;
            callback.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("TAIL"))
    private void onScroll$1(long windowPointer, double xOffset, double yOffset, CallbackInfo callback) {
        if (windowPointer == Minecraft.getInstance().getWindow().getWindow() && this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen == null && this.minecraft.player != null) {
                Objects.requireNonNull(this.puzzleslib$verticalScrollAmount, "vertical scroll amount is null");
                Objects.requireNonNull(this.puzzleslib$horizontalScrollAmount, "horizontal scroll amount is null");
                FabricClientEvents.AFTER_MOUSE_SCROLL.invoker().onAfterMouseScroll(this.isLeftPressed(), this.isMiddlePressed(), this.isRightPressed(), this.puzzleslib$horizontalScrollAmount, this.puzzleslib$verticalScrollAmount);
                this.puzzleslib$verticalScrollAmount = null;
                this.puzzleslib$horizontalScrollAmount = null;
            }
        }
    }

    @Shadow
    public abstract boolean isLeftPressed();

    @Shadow
    public abstract boolean isMiddlePressed();

    @Shadow
    public abstract boolean isRightPressed();

    @SuppressWarnings("target")
    @Inject(method = "lambda$onMove$11(Lnet/minecraft/client/gui/screens/Screen;DDDD)V", at = @At("HEAD"), cancellable = true)
    private void lambda$onMove$11$0(Screen screen, double mouseX, double mouseY, double dragX, double dragY, CallbackInfo callback) {

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
    @Inject(method = "lambda$onMove$11(Lnet/minecraft/client/gui/screens/Screen;DDDD)V", at = @At("TAIL"))
    private void lambda$onMove$11$1(Screen screen, double mouseX, double mouseY, double dragX, double dragY, CallbackInfo callback) {

        if (this.puzzleslib$currentScreen == null) return;

        // On Forge this only runs when Screen::mouseDragged returns false, but vanilla does not capture the result from that method invocation.
        // We can't just call the method ourselves, as that would require replacing the vanilla invocation, which messed with other mods placing their own hook here (namely Mouse Tweaks),
        // so there is no way of knowing if vanilla was successful on Fabric right now.
        ExtraScreenMouseEvents.afterMouseDrag(this.puzzleslib$currentScreen).invoker().afterMouseDrag(this.puzzleslib$currentScreen, mouseX, mouseY, this.activeButton, dragX, dragY);
        this.puzzleslib$currentScreen = null;
    }
}
