package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.fabric.api.client.event.v1.AfterBackgroundCallback;
import net.fabricmc.fabric.api.event.Event;

@Deprecated
public interface ExtraScreenExtensions {

    Event<AfterBackgroundCallback> puzzleslib$getAfterBackgroundEvent();
}
