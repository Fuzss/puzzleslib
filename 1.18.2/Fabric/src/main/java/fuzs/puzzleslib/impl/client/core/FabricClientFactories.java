package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import fuzs.puzzleslib.api.client.screen.v2.ScreenHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.client.init.FabricItemDisplayOverrides;
import fuzs.puzzleslib.impl.client.screen.FabricKeyMappingActivationHelper;
import fuzs.puzzleslib.impl.client.screen.FabricScreenHelper;

import java.util.Set;

public final class FabricClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        FabricClientModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ScreenHelper getScreenHelper() {
        return new FabricScreenHelper();
    }

    @Override
    public ItemModelDisplayOverrides getItemModelDisplayOverrides() {
        return new FabricItemDisplayOverrides();
    }

    @Override
    public KeyMappingActivationHelper getKeyMappingActivationHelper() {
        return new FabricKeyMappingActivationHelper();
    }
}
