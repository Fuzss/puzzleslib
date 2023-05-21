package fuzs.puzzleslib.api.capability.v2.data;

import java.util.function.Function;

/**
 * helper interface for creating capability factories
 * @param <C> serializable capability type
 */
@FunctionalInterface
public interface CapabilityFactory<C> extends Function<Object, C> {

    /**
     * @param t object to create capability from, mostly unused
     * @return the capability component
     */
    C createComponent(Object t);

    @Override
    default C apply(Object t) {
        return this.createComponent(t);
    }
}
