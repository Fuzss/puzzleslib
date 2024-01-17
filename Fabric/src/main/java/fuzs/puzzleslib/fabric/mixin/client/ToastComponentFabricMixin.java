package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class)
abstract class ToastComponentFabricMixin {

    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    public void addToast(Toast toast, CallbackInfo callback) {
        EventResult result = FabricGuiEvents.ADD_TOAST.invoker().onAddToast(ToastComponent.class.cast(this), toast);
        if (result.isInterrupt()) callback.cancel();
    }
}
