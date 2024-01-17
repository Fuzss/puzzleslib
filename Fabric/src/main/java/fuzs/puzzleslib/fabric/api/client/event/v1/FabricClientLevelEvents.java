package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricClientLevelEvents {
    /**
     * Fires before a client level is loaded.
     */
    public static final Event<ClientLevelEvents.Load> LOAD_LEVEL = FabricEventFactory.create(ClientLevelEvents.Load.class);
    /**
     * Fires before a client level is unloaded.
     */
    public static final Event<ClientLevelEvents.Unload> UNLOAD_LEVEL = FabricEventFactory.create(ClientLevelEvents.Unload.class);

    private FabricClientLevelEvents() {

    }
}
