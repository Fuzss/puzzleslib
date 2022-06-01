package fuzs.puzzleslib.capability.data;

/**
 * helper interface for creating capability factories
 * @param <C> serializable capability type
 */
@FunctionalInterface
public interface CapabilityFactory<C extends CapabilityComponent> {

    /**
     * @param t object to create capability from, mostly unused
     * @return the capability component
     */
    C createComponent(Object t);
}
