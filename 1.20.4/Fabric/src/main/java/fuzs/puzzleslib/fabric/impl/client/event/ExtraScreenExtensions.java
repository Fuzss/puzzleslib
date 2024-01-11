package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.fabric.api.client.event.v1.ExtraScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;

public interface ExtraScreenExtensions {

    Event<ExtraScreenMouseEvents.AllowMouseDrag> puzzleslib$getAllowMouseDragEvent();

    Event<ExtraScreenMouseEvents.BeforeMouseDrag> puzzleslib$getBeforeMouseDragEvent();

    Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$getAfterMouseDragEvent();
}
