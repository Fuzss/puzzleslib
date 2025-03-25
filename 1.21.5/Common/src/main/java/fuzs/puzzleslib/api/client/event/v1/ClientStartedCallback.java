package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;

@FunctionalInterface
public interface ClientStartedCallback {
    EventInvoker<ClientStartedCallback> EVENT = EventInvoker.lookup(ClientStartedCallback.class);

    /**
     * Called the client has started and is about to tick for the first time.
     *
     * @param minecraft the minecraft singleton instance
     */
    void onClientStarted(Minecraft minecraft);
}
