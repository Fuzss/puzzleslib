package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.server.AddDataPackReloadListenersCallback;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricLifecycleEvents {
    /**
     * Fires when mod loading is complete.
     */
    public static final Event<LoadCompleteCallback> LOAD_COMPLETE = FabricEventFactory.create(LoadCompleteCallback.class);
    /**
     * Adds a listener to the server resource manager (for data packs) to reload at the end of all resources.
     */
    public static final Event<AddDataPackReloadListenersCallback> ADD_DATA_PACK_RELOAD_LISTENERS = FabricEventFactory.create(
            AddDataPackReloadListenersCallback.class);

    private FabricLifecycleEvents() {
        // NO-OP
    }
}
