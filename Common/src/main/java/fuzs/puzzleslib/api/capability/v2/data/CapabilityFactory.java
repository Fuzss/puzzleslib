package fuzs.puzzleslib.api.capability.v2.data;

import java.util.function.Function;

/**
 * helper interface for creating capability factories
 * @param <C> serializable capability type
 */
@FunctionalInterface
public interface CapabilityFactory<C> extends Function<Object, C> {

    @Override
    C apply(Object t);
}
