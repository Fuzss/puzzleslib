package fuzs.puzzleslib.impl.capability;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.impl.capability.data.ComponentHolder;

import java.util.function.Function;

public interface ComponentFactoryRegistry<T> {

    void accept(Object o, ComponentKey<ComponentHolder> componentKey, Function<T, ComponentHolder> factory);
}
