package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.gui.screens.CommonScreens;
import fuzs.puzzleslib.core.CoreServices;

import static fuzs.puzzleslib.util.PuzzlesUtil.loadServiceProvider;

/**
 * services which may only be loaded on the client side
 *
 * <p>TODO don't extend CoreServices + make final
 */
public class ClientCoreServices extends CoreServices {
    /**
     * important client exclusive factories
     */
    public static final ClientFactories FACTORIES = loadServiceProvider(ClientFactories.class);
    /**
     * a helper class for dealing with instances of {@link net.minecraft.client.gui.screens.Screen}
     */
    public static final CommonScreens SCREENS = loadServiceProvider(CommonScreens.class);
}
