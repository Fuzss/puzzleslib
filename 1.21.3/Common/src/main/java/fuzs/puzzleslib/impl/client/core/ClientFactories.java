package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;

import java.util.Set;

public interface ClientFactories {
    ClientFactories INSTANCE = ServiceProviderHelper.load(ClientFactories.class);

    void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle);

    ItemDisplayOverridesImpl getItemModelDisplayOverrides();

    KeyMappingHelper getKeyMappingActivationHelper();
}
