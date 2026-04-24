package fuzs.puzzleslib.common.api.client.event.v1;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface ClientSetupCallback {
    EventInvoker<ClientSetupCallback> EVENT = EventInvoker.lookup(ClientSetupCallback.class);

    /**
     * Used to set various values and settings for already registered content.
     *
     * @see ClientModConstructor#onClientSetup()
     */
    void onClientSetup();
}
