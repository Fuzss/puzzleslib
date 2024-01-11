package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
abstract class KeyboardHandlerFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    public void keyPress$0(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callback) {
        if (windowPointer == this.minecraft.getWindow().getWindow()) {
            EventResult result = FabricClientEvents.BEFORE_KEY_ACTION.invoker().onBeforeKeyAction(key, scanCode, action, modifiers);
            if (result.isInterrupt()) callback.cancel();
        }
    }

    @Inject(method = "keyPress", at = @At("TAIL"), cancellable = true)
    public void keyPress$1(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callback) {
        if (windowPointer == this.minecraft.getWindow().getWindow()) {
            FabricClientEvents.AFTER_KEY_ACTION.invoker().onAfterKeyAction(key, scanCode, action, modifiers);
        }
    }
}
