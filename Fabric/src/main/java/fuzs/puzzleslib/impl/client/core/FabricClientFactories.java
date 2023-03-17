package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.impl.client.screens.FabricScreenHelper;
import fuzs.puzzleslib.api.client.screens.v2.ScreenHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;

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
