package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.impl.client.event.ItemDecoratorRegistryImpl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
abstract class GuiGraphicsFabricMixin {

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    public void renderItemDecorations(Font font, ItemStack itemStack, int posX, int posY, @Nullable String text, CallbackInfo callback) {
        if (!itemStack.isEmpty()) {
            ItemDecoratorRegistryImpl.render(GuiGraphics.class.cast(this), font, itemStack, posX, posY);
        }
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(Font font, List<ClientTooltipComponent> tooltipComponents, int mouseX, int mouseY, ClientTooltipPositioner clientTooltipPositioner, @Nullable ResourceLocation resourceLocation, CallbackInfo callback) {
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
