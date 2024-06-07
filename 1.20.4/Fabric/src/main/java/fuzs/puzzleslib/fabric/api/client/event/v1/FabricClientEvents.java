package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricClientEvents {
    /**
     * Called before a mouse button is clicked or released without a screen being open.
     */
    public static final Event<InputEvents.BeforeMouseAction> BEFORE_MOUSE_ACTION = FabricEventFactory.createResult(
            InputEvents.BeforeMouseAction.class);
    /**
     * Called after a mouse button is clicked or released without a screen being open.
     */
    public static final Event<InputEvents.AfterMouseAction> AFTER_MOUSE_ACTION = FabricEventFactory.create(InputEvents.AfterMouseAction.class);
    /**
     * Called before a mouse has scrolled without a screen being open.
     */
    public static final Event<InputEvents.BeforeMouseScroll> BEFORE_MOUSE_SCROLL = FabricEventFactory.createResult(
            InputEvents.BeforeMouseScroll.class);
    /**
     * Called after a mouse has scrolled without a screen being open.
     */
    public static final Event<InputEvents.AfterMouseScroll> AFTER_MOUSE_SCROLL = FabricEventFactory.create(InputEvents.AfterMouseScroll.class);
    /**
     * Called before a key press, release or repeat action is handled.
     */
    public static final Event<InputEvents.BeforeKeyAction> BEFORE_KEY_ACTION = FabricEventFactory.createResult(
            InputEvents.BeforeKeyAction.class);
    /**
     * Called after a key press, release or repeat action is handled.
     */
    public static final Event<InputEvents.AfterKeyAction> AFTER_KEY_ACTION = FabricEventFactory.create(InputEvents.AfterKeyAction.class);

    private FabricClientEvents() {

    }
}
