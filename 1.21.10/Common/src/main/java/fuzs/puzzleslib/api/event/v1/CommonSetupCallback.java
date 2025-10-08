package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface CommonSetupCallback {
    EventInvoker<CommonSetupCallback> EVENT = EventInvoker.lookup(CommonSetupCallback.class);

    /**
     * Used to set various values and settings for already registered content.
     *
     * @see ModConstructor#onCommonSetup()
     */
    void onCommonSetup();
}
