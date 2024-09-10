package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.InputEvents;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricClientEvents {
    /**
     * Called before a mouse button is clicked or released without a screen being open.
     */
    public static final Event<InputEvents.MouseClick> MOUSE_CLICK = FabricEventFactory.createResult(
            InputEvents.MouseClick.class);
    /**
     * Called before a mouse has scrolled without a screen being open.
     */
    public static final Event<InputEvents.MouseScroll> MOUSE_SCROLL = FabricEventFactory.createResult(
            InputEvents.MouseScroll.class);
    /**
     * Called before a key press, release or repeat action is handled.
     */
    public static final Event<InputEvents.KeyPress> KEY_PRESS = FabricEventFactory.createResult(
            InputEvents.KeyPress.class);
    /**
     * Fired after the resource manager has reloaded models.
     */
    public static final Event<ModelEvents.CompleteModelLoading> COMPLETE_MODEL_LOADING = FabricEventFactory.create(
            ModelEvents.CompleteModelLoading.class);

    private FabricClientEvents() {
        // NO-OP
    }
}
