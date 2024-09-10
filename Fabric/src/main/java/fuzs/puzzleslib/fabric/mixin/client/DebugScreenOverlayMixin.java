package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
abstract class DebugScreenOverlayMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "drawGameInformation", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
    )
    )
    protected void drawGameInformation(GuiGraphics guiGraphics, CallbackInfo callback, @Local List<String> list) {
        FabricGuiEvents.GATHER_LEFT_DEBUG_TEXT.invoker()
                .onGatherLeftDebugText(this.minecraft.getWindow(), guiGraphics, this.minecraft.getTimer(), list);
    }

    @Inject(
            method = "drawSystemInformation", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
    )
    )
    protected void drawSystemInformation(GuiGraphics guiGraphics, CallbackInfo callback, @Local List<String> list) {
        FabricGuiEvents.GATHER_RIGHT_DEBUG_TEXT.invoker()
                .onGatherRightDebugText(this.minecraft.getWindow(), guiGraphics, this.minecraft.getTimer(), list);
    }
}
