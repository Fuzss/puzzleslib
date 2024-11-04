package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricLifecycleEvents {
    /**
     * Fires when mod loading is complete.
     */
    public static final Event<LoadCompleteCallback> LOAD_COMPLETE = FabricEventFactory.create(
            LoadCompleteCallback.class);

    private FabricLifecycleEvents() {
        // NO-OP
    }
}
