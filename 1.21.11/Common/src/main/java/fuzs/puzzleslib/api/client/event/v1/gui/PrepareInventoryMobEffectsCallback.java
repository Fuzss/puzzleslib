package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface PrepareInventoryMobEffectsCallback {
    EventInvoker<PrepareInventoryMobEffectsCallback> EVENT = EventInvoker.lookup(PrepareInventoryMobEffectsCallback.class);

    /**
     * Called before mob effects are drawn next to the inventory menu, used to force a rendering mode; or to cancel the
     * rendering completely.
     *
     * @param screen             the screen drawing mob effect widgets
     * @param maxWidth           the space available to the right of the menu
     * @param smallWidgets       is the compact square rendering mode selected by vanilla; otherwise the full size mode
     *                           is used
     * @param horizontalPosition the offset for the widgets to render from the left side of the window (this is an
     *                           absolute value), which by default is 2 pixels to the right of the right menu border
     *                           ({@code this.leftPos + this.imageWidth + 2})
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent any mob effect widgets from appearing</li>
     *         <li>{@link EventResult#PASS PASS} to let vanilla behaviour continue, using values set by this event</li>
     *         </ul>
     */
    EventResult onPrepareInventoryMobEffects(Screen screen, int maxWidth, MutableBoolean smallWidgets, MutableInt horizontalPosition);
}
