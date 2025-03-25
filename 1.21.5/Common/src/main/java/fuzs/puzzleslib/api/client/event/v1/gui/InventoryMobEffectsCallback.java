package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface InventoryMobEffectsCallback {
    EventInvoker<InventoryMobEffectsCallback> EVENT = EventInvoker.lookup(InventoryMobEffectsCallback.class);

    /**
     * Called before mob effects are drawn next to the inventory menu, used to force a rendering mode, or to cancel the rendering completely.
     *
     * @param screen           the screen drawing mob effect widgets
     * @param availableSpace   space available to the right of the menu
     * @param smallWidgets     is compact square rendering mode selected by vanilla, otherwise the full size mode is used
     * @param horizontalOffset the offset for the widgets to render from the left side of the window (this is an absolute value),
     *                         which by default is 2 pixels to the right of the right menu border (<code>this.leftPos + this.imageWidth + 2</code>)
     * @return {@link EventResult#INTERRUPT} to prevent any mob effect widgets from appearing,
     * {@link EventResult#PASS} to let vanilla behavior continue, using values set for <code>compactRendering</code> and <code>horizontalOffset</code>
     */
    EventResult onInventoryMobEffects(Screen screen, int availableSpace, MutableBoolean smallWidgets, MutableInt horizontalOffset);
}
