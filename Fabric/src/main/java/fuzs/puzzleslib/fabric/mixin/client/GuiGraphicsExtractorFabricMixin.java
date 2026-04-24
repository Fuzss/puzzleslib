package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
abstract class GuiGraphicsExtractorFabricMixin {

    @Inject(method = "tooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(Font font, List<ClientTooltipComponent> lines, int mouseX, int mouseY, ClientTooltipPositioner positioner, @Nullable Identifier style, CallbackInfo callback) {
        if (!lines.isEmpty()) {
            EventResult result = FabricGuiEvents.RENDER_TOOLTIP.invoker()
                    .onRenderTooltip(GuiGraphicsExtractor.class.cast(this), font, mouseX, mouseY, lines, positioner);
            if (result.isInterrupt()) {
                callback.cancel();
            }
        }
    }
}
