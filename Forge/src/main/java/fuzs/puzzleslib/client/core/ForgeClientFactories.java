package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.gui.screens.ForgeScreens;
import fuzs.puzzleslib.client.gui.screens.Screens;

import java.util.function.Consumer;

/**
 * Forge client factories
 */
public class ForgeClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId) {
        return constructor -> ForgeClientModConstructor.construct(modId, constructor);
    }

    @Override
    public Screens screens() {
        return ForgeScreens.INSTANCE;
    }
}
