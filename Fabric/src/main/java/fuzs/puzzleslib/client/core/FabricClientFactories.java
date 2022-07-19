package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.gui.screens.FabricScreens;
import fuzs.puzzleslib.client.gui.screens.Screens;

import java.util.function.Consumer;

/**
 * Fabric client factories
 */
public class FabricClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId) {
        return FabricClientModConstructor::construct;
    }

    @Override
    public Screens screens() {
        return FabricScreens.INSTANCE;
    }
}
