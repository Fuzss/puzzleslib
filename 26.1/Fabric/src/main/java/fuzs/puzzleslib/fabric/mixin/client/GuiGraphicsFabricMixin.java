package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
abstract class GuiGraphicsFabricMixin {

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(Font font, List<ClientTooltipComponent> tooltipComponents, int mouseX, int mouseY, ClientTooltipPositioner clientTooltipPositioner, @Nullable Identifier identifier, CallbackInfo callback) {
        if (!tooltipComponents.isEmpty()) {
            EventResult result = FabricGuiEvents.RENDER_TOOLTIP.invoker()
                    .onRenderTooltip(GuiGraphics.class.cast(this),
                            font,
                            mouseX,
                            mouseY,
                            tooltipComponents,
                            clientTooltipPositioner);
            if (result.isInterrupt()) callback.cancel();
        }
    }
}
