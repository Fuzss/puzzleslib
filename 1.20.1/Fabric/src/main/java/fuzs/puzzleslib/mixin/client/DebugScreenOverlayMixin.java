package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
abstract class DebugScreenOverlayMixin {

    @Inject(method = "renderLines", at = @At("HEAD"))
    private void renderLines(GuiGraphics guiGraphics, List<String> lines, boolean leftSide, CallbackInfo callback) {
        if (leftSide) {
            FabricClientEvents.GATHER_LEFT_DEBUG_TEXT.invoker().onGatherLeftDebugText(lines);
        } else {
            FabricClientEvents.GATHER_RIGHT_DEBUG_TEXT.invoker().onGatherRightDebugText(lines);
        }
    }
}
