package fuzs.puzzleslib.fabric.impl.capability;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.fabric.impl.capability.data.ComponentHolder;

import java.util.function.Function;

public interface ComponentFactoryRegistry<T> {

    void accept(Object o, ComponentKey<ComponentHolder> componentKey, Function<T, ComponentHolder> factory);
}
