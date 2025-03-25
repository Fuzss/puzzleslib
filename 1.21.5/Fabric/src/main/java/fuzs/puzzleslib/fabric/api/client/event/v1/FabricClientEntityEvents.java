package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

public final class FabricClientEntityEvents {
    /**
     * Fired when an entity is added to the level on the client.
     * <p>
     * We do not use {@link net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents#ENTITY_LOAD} as it does
     * not allow for preventing the entity from being added.
     */
    public static final Event<ClientEntityLevelEvents.Load> ENTITY_LOAD = FabricEventFactory.createResult(
            ClientEntityLevelEvents.Load.class);

    private FabricClientEntityEvents() {
        // NO-OP
    }
}
