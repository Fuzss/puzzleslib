package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.fabric.impl.client.init.FabricItemDisplayOverrides;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingHelper;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;

import java.util.Set;

public final class FabricClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        FabricClientModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ItemDisplayOverridesImpl getItemModelDisplayOverrides() {
        return new FabricItemDisplayOverrides();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new FabricKeyMappingHelper();
    }
}
