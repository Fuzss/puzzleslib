package fuzs.puzzleslib.impl.event.core;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import org.jetbrains.annotations.Nullable;

public interface EventInvokerLike<T> {

    EventInvoker<T> asEventInvoker(@Nullable Object context);
}
