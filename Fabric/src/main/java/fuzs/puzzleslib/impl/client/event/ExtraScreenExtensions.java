package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.ExtraScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;

public interface ExtraScreenExtensions {

    Event<ExtraScreenMouseEvents.AllowMouseDrag> puzzleslib$getAllowMouseDragEvent();

    Event<ExtraScreenMouseEvents.BeforeMouseDrag> puzzleslib$getBeforeMouseDragEvent();

    Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$getAfterMouseDragEvent();
}
