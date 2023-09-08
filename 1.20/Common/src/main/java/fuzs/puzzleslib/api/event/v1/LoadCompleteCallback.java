package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LoadCompleteCallback {
    EventInvoker<LoadCompleteCallback> EVENT = EventInvoker.lookup(LoadCompleteCallback.class);

    static EventInvoker<LoadCompleteCallback> loadComplete(@Nullable String modId) {
        return EventInvoker.lookup(LoadCompleteCallback.class, modId);
    }

    /**
     * Fires when mod loading is complete and the current game instance is about to tick for the very first time.
     */
    void onLoadComplete();
}
