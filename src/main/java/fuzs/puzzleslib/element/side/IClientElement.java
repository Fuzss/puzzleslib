package fuzs.puzzleslib.element.side;

import fuzs.puzzleslib.config.option.OptionsBuilder;
import net.minecraft.client.Minecraft;

/**
 * implement this for elements with client-side capabilities
 */
public interface IClientElement extends ISidedElement {

    /**
     * register client events
     */
    default void constructClient() {

        this.setupClient();
    }

    /**
     * register client events
     */
    @Deprecated
    default void setupClient() {

    }

    /**
     * TODO rename to #setupClient
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}
     * is always called no matter the element's state
     */
    default void setupClient2() {

    }

    /**
     * called when the element is enabled and on launch
     */
    default void loadClient() {

    }

    /**
     * called when the element is disabled
     */
    default void unloadClient() {

    }

    /**
     * build client config
     * @param builder builder for client config
     */
    default void setupClientConfig(OptionsBuilder builder) {

    }

    /**
     * @return description for this elements client config section
     */
    default String[] getClientDescription() {

        return new String[0];
    }

    /**
     * @return Minecraft client instance
     */
    @Deprecated
    static Minecraft getMc() {

        return Minecraft.getInstance();
    }

}
