package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface ClientSetupCallback {
    EventInvoker<ClientSetupCallback> EVENT = EventInvoker.lookup(ClientSetupCallback.class);

    /**
     * Used to set various values and settings for already registered content.
     * <p>
     * Same as {@link ClientModConstructor#onClientSetup()}, but in event form.
     */
    void onClientSetup();
}
