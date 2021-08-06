package fuzs.puzzleslib.element.side;

import fuzs.puzzleslib.config.option.OptionBuilder;

/**
 * implement this for elements with common capabilities
 */
public interface ICommonElement extends ISidedElement {

    /**
     * register common events and registry entry objects (blocks, items, etc.)
     */
    default void constructCommon() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
     * is always loaded no matter the element's state
     */
    default void setupCommon() {

    }

    /**
     * called when the element is enabled and on launch
     */
    default void loadCommon() {

    }

    /**
     * called when the element is disabled
     */
    default void unloadCommon() {

    }

    /**
     * build common config
     * @param builder builder for common config
     */
    default void setupCommonConfig(OptionBuilder builder) {

    }

    /**
     * @return description for this elements common config section
     */
    default String[] getCommonDescription() {

        return new String[0];
    }

}
