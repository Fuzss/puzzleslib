package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.gui.screens.Screens;
import fuzs.puzzleslib.core.Services;

/**
 * services which may only be loaded on the client side
 */
public class ClientServices extends Services {
    /**
     * a helper class for dealing with instances of {@link net.minecraft.client.gui.screens.Screen}
     */
    public static final Screens SCREENS = load(Screens.class);
    /**
     * a collection of utility methods for registering client side content
     */
    public static final ClientRegistration CLIENT_REGISTRATION = load(ClientRegistration.class);
}
