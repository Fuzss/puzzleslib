package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.resources.ResourceLocation;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class FabricGuiEvents {
    private static final Map<String, Event<RenderGuiLayerEvents.Before>> BEFORE_RENDER_GUI_ELEMENT_EVENTS = new IdentityHashMap<>();
    private static final Map<String, Event<RenderGuiLayerEvents.After>> AFTER_RENDER_GUI_ELEMENT_EVENTS = new IdentityHashMap<>();
    /**
     * Called for instance of {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}, after the
     * screen background is drawn (like menu texture).
     */
    public static final Event<ContainerScreenEvents.Background> CONTAINER_SCREEN_BACKGROUND = FabricEventFactory.create(
            ContainerScreenEvents.Background.class);
    /**
     * Called for instance of {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}, after the
     * screen foreground is drawn (like text labels).
     */
    public static final Event<ContainerScreenEvents.Foreground> CONTAINER_SCREEN_FOREGROUND = FabricEventFactory.create(
            ContainerScreenEvents.Foreground.class);
    /**
     * Called before mob effects are drawn next to the inventory menu, used to force a rendering mode, or to cancel the
     * rendering completely.
     */
    public static final Event<InventoryMobEffectsCallback> INVENTORY_MOB_EFFECTS = FabricEventFactory.createResult(
            InventoryMobEffectsCallback.class);
    /**
     * Called just before a new screen is set to {@link net.minecraft.client.Minecraft#screen} in
     * {@link net.minecraft.client.Minecraft#setScreen}, allows for exchanging the new screen with a different one, or
     * can prevent a new screen from opening, effectively forcing the old screen to remain.
     */
    public static final Event<ScreenOpeningCallback> SCREEN_OPENING = FabricEventFactory.createResult(
            ScreenOpeningCallback.class);
    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     */
    public static final Event<RenderTooltipCallback> RENDER_TOOLTIP = FabricEventFactory.createResult(
            RenderTooltipCallback.class);
    /**
     * Called before the chat panel is drawn, allows for changing x- and y-position.
     */
    public static final Event<CustomizeChatPanelCallback> CUSTOMIZE_CHAT_PANEL = FabricEventFactory.create(
            CustomizeChatPanelCallback.class);
    /**
     * Fires when a {@link Toast} is about to be queued in
     * {@link net.minecraft.client.gui.components.toasts.ToastComponent#addToast(Toast)}.
     */
    public static final Event<AddToastCallback> ADD_TOAST = FabricEventFactory.createResult(AddToastCallback.class);
    /**
     * An event that runs just before rendering all game information text lines on the left side of the debug screen
     * overlay in {@link net.minecraft.client.gui.components.DebugScreenOverlay#drawGameInformation(GuiGraphics)}.
     */
    public static final Event<GatherDebugTextEvents.Left> GATHER_LEFT_DEBUG_TEXT = FabricEventFactory.create(
            GatherDebugTextEvents.Left.class);
    /**
     * An event that runs just before rendering all system information text lines on the right side of the debug screen
     * overlay in {@link net.minecraft.client.gui.components.DebugScreenOverlay#drawSystemInformation(GuiGraphics)}.
     */
    public static final Event<GatherDebugTextEvents.Right> GATHER_RIGHT_DEBUG_TEXT = FabricEventFactory.create(
            GatherDebugTextEvents.Right.class);
    /**
     * Called when tooltip lines are gathered for an effect widget in the player inventory when it is hovered by the
     * cursor.
     */
    public static final Event<GatherEffectScreenTooltipCallback> GATHER_EFFECT_SCREEN_TOOLTIP = FabricEventFactory.create(
            GatherEffectScreenTooltipCallback.class);
    /**
     * Called at the beginning of {@link net.minecraft.client.gui.Gui#render(GuiGraphics, DeltaTracker)}, before vanilla
     * has drawn any gui elements.
     */
    public static final Event<RenderGuiEvents.Before> BEFORE_RENDER_GUI = FabricEventFactory.create(
            RenderGuiEvents.Before.class);

    private FabricGuiEvents() {
        // NO-OP
    }

    /**
     * Called before a gui element is rendered, allows for cancelling rendering.
     *
     * @param resourceLocation id of the gui element, all vanilla ids can be found in {@link RenderGuiLayerEvents}
     * @return the event instance
     */
    public static Event<RenderGuiLayerEvents.Before> beforeRenderGuiElement(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return BEFORE_RENDER_GUI_ELEMENT_EVENTS.computeIfAbsent(resourceLocation.toString().intern(), $ -> {
            return FabricEventFactory.createResult(RenderGuiLayerEvents.Before.class);
        });
    }

    /**
     * Called after a gui element is rendered.
     *
     * @param resourceLocation id of the gui element, all vanilla ids can be found in {@link RenderGuiLayerEvents}
     * @return the event instance
     */
    public static Event<RenderGuiLayerEvents.After> afterRenderGuiElement(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return AFTER_RENDER_GUI_ELEMENT_EVENTS.computeIfAbsent(resourceLocation.toString().intern(), $ -> {
            return FabricEventFactory.create(RenderGuiLayerEvents.After.class);
        });
    }
}
