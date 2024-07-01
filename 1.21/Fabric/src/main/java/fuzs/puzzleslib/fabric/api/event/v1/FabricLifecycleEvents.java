package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricLifecycleEvents {
    /**
     * Fires when mod loading is complete on a dedicated server.
     * <p>
     * Invocation on dedicated client is implemented via
     * {@link net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents#CLIENT_STARTED}.
     */
    public static final Event<LoadCompleteCallback> SERVER_LOAD_COMPLETE = FabricEventFactory.create(
            LoadCompleteCallback.class);

    private FabricLifecycleEvents() {

    }
}
