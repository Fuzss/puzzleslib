package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
abstract class MouseHandlerFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onButton",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
                    ordinal = 0),
            cancellable = true)
    private void onButton(long windowPointer, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo callback) {
        EventResult eventResult = FabricClientEvents.MOUSE_CLICK.invoker().onMouseClick(mouseButtonInfo, action);
        if (eventResult.isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "onScroll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            cancellable = true)
    private void onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo callback) {
        // just recalculate this instead of capturing local, shouldn't be able to change in the meantime
        boolean discreteMouseScroll = this.minecraft.options.discreteMouseScroll().get();
        double mouseWheelSensitivity = this.minecraft.options.mouseWheelSensitivity().get();
        double horizontalScrollAmount = discreteMouseScroll ? Math.signum(xOffset) : xOffset * mouseWheelSensitivity;
        double verticalScrollAmount = discreteMouseScroll ? Math.signum(yOffset) : yOffset * mouseWheelSensitivity;
        EventResult eventResult = FabricClientEvents.MOUSE_SCROLL.invoker()
                .onMouseScroll(this.isLeftPressed(),
                        this.isMiddlePressed(),
                        this.isRightPressed(),
                        horizontalScrollAmount,
                        verticalScrollAmount);
        if (eventResult.isInterrupt()) {
            callback.cancel();
        }
    }

    @Shadow
    public abstract boolean isLeftPressed();

    @Shadow
    public abstract boolean isMiddlePressed();

    @Shadow
    public abstract boolean isRightPressed();
}
