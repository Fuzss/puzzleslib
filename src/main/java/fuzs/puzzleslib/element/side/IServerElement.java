package fuzs.puzzleslib.element.side;

import fuzs.puzzleslib.config.option.OptionBuilder;

/**
 * implement this for elements with server-side capabilities
 */
public interface IServerElement extends ISidedElement {

    /**
     * register server events
     */
    default void constructServer() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent}
     * is always loaded no matter the element's state
     */
    default void setupServer() {

    }

    /**
     * called when the element is enabled and on launch
     */
    default void loadServer() {

    }

    /**
     * called when the element is disabled
     */
    default void unloadServer() {

    }

    /**
     * build server config
     * @param builder builder for server config
     */
    default void setupServerConfig(OptionBuilder builder) {

    }

    /**
     * @return description for this elements server config section
     */
    default String[] getServerDescription() {

        return new String[0];
    }

}
