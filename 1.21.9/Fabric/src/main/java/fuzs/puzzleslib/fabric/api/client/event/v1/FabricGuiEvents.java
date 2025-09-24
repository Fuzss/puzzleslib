package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class FabricGuiEvents {
    /**
     * Called for {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen AbstractContainerScreens},
     * after the screen foreground is drawn (like text labels) via
     * {@link AbstractContainerScreen#renderContents(GuiGraphics, int, int, float)}.
     */
    public static final Event<RenderContainerScreenContentsCallback> RENDER_CONTAINER_SCREEN_CONTENTS = FabricEventFactory.create(
            RenderContainerScreenContentsCallback.class);
    /**
     * Called before mob effects are drawn next to the inventory menu, used to force a rendering mode; or to cancel the
     * rendering completely.
     */
    public static final Event<PrepareInventoryMobEffectsCallback> INVENTORY_MOB_EFFECTS = FabricEventFactory.createResult(
            PrepareInventoryMobEffectsCallback.class);
    /**
     * Called just before a new screen is set to {@link net.minecraft.client.Minecraft#screen} in
     * {@link net.minecraft.client.Minecraft#setScreen}, allows for exchanging the new screen with a different one, or
     * can prevent a new screen from opening, effectively forcing the old screen to remain.
     */
    public static final Event<ScreenOpeningCallback> SCREEN_OPENING = FabricEventFactory.createResultHolder(
            ScreenOpeningCallback.class);
    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     */
    public static final Event<RenderTooltipCallback> RENDER_TOOLTIP = FabricEventFactory.createResult(
            RenderTooltipCallback.class);
    /**
     * Fires when a {@link Toast} is about to be queued in
     * {@link net.minecraft.client.gui.components.toasts.ToastManager#addToast(Toast)}.
     */
    public static final Event<AddToastCallback> ADD_TOAST = FabricEventFactory.createResult(AddToastCallback.class);
    /**
     * Called when tooltip lines are gathered for an effect widget in the player inventory when it is hovered by the
     * cursor.
     */
    public static final Event<GatherEffectScreenTooltipCallback> GATHER_EFFECT_SCREEN_TOOLTIP = FabricEventFactory.create(
            GatherEffectScreenTooltipCallback.class);

    private FabricGuiEvents() {
        // NO-OP
    }
}
