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
}
