package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
abstract class DebugScreenOverlayMixin {

    @Inject(method = "getGameInformation", at = @At("RETURN"))
    protected void getGameInformation(CallbackInfoReturnable<List<String>> callback) {
        FabricGuiEvents.GATHER_GAME_INFORMATION_DEBUG_TEXT.invoker().onGatherGameInformation(callback.getReturnValue());
    }

    @Inject(method = "getSystemInformation", at = @At("RETURN"))
    protected void getSystemInformation(CallbackInfoReturnable<List<String>> callback) {
        FabricGuiEvents.GATHER_SYSTEM_INFORMATION_DEBUG_TEXT.invoker()
                .onGatherSystemInformation(callback.getReturnValue());
    }
}
