package fuzs.puzzleslib.fabric.impl.capability;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.fabric.impl.capability.data.ComponentAdapter;

import java.util.function.Function;

@FunctionalInterface
public interface ComponentFactoryRegistrar<T, C extends CapabilityComponent<T>> {

    void accept(Object o, ComponentKey<ComponentAdapter<T, C>> componentKey, Function<T, ComponentAdapter<T, C>> factory);
}
