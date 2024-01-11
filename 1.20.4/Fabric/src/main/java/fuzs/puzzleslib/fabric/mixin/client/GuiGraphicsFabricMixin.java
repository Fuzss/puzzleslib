package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricScreenEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.impl.client.event.ItemDecoratorRegistryImpl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
abstract class GuiGraphicsFabricMixin {

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    public void renderItemDecorations(Font font, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo callback) {
        if (!stack.isEmpty()) ItemDecoratorRegistryImpl.render(GuiGraphics.class.cast(this), font, stack, xPosition, yPosition);
    }

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    private void renderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo callback) {
        if (components.isEmpty()) return;
        EventResult result = FabricScreenEvents.RENDER_TOOLTIP.invoker().onRenderTooltip(GuiGraphics.class.cast(this), mouseX, mouseY, this.guiWidth(), this.guiHeight(), font, components, clientTooltipPositioner);
        if (result.isInterrupt()) callback.cancel();
    }

    @Shadow
    public abstract int guiWidth();

    @Shadow
    public abstract int guiHeight();
}
