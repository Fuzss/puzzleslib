package fuzs.puzzleslib.element.v2;

import fuzs.puzzleslib.element.AbstractElement;

/**
 * provides features for setting up {@link ModElement} with config options
 */
public interface ConfigElement {
    /**
     * @return is the element enabled
     */
    boolean isEnabled();

    /**
     * @return is the element enabled by default
     */
    boolean isEnabledByDefault();

    /**
     * @return name of this element
     */
    String getDisplayName();

    /**
     * @return description for this element
     */
    String[] getDescription();
}
