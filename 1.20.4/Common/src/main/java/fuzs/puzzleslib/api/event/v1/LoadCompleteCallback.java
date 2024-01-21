package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface LoadCompleteCallback {
    EventInvoker<LoadCompleteCallback> EVENT = EventInvoker.lookup(LoadCompleteCallback.class);

    /**
     * Fires when mod loading is complete and the current game instance is about to tick for the very first time.
     * <p>Runs a little bit later for dedicated servers on Fabric due to the absence of an earlier event in the api.
     */
    void onLoadComplete();
}
