package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.gui.screens.FabricScreenHelper;
import fuzs.puzzleslib.client.gui.screens.ScreenHelper;
import fuzs.puzzleslib.core.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.client.core.ClientFactories;

import java.util.function.Supplier;

public final class FabricClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, Supplier<ClientModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations) {
        FabricClientModConstructor.construct(modConstructor.get(), modId, contentRegistrations);
    }

    @Override
    public ScreenHelper getScreenHelper() {
        return new FabricScreenHelper();
    }
}
