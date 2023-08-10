package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.api.client.screen.v2.ScreenHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;

import java.util.Set;

public interface ClientFactories {
    ClientFactories INSTANCE = ServiceProviderHelper.load(ClientFactories.class);

    void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle);

    ScreenHelper getScreenHelper();

    ItemModelDisplayOverrides getItemModelDisplayOverrides();
}
