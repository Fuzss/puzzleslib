package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface LoadCompleteCallback {
    EventInvoker<LoadCompleteCallback> EVENT = EventInvoker.lookup(LoadCompleteCallback.class);

    /**
     * Fires when mod loading is complete.
     * <p>
     * The event runs sequentially as opposed to the rest of mod loading on Forge &amp; NeoForge which is executed in
     * parallel.
     */
    void onLoadComplete();
}
