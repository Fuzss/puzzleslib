package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import fuzs.puzzleslib.api.client.screen.v2.ScreenHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.neoforge.impl.client.screen.NeoForgeScreenHelper;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.neoforge.impl.client.init.NeoForgeItemDisplayOverrides;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.neoforge.impl.client.screen.NeoForgeKeyMappingActivationHelper;

import java.util.Set;

public final class NeoForgeClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        NeoForgeClientModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ScreenHelper getScreenHelper() {
        return new NeoForgeScreenHelper();
    }

    @Override
    public ItemDisplayOverridesImpl getItemModelDisplayOverrides() {
        return new NeoForgeItemDisplayOverrides();
    }

    @Override
    public KeyMappingActivationHelper getKeyMappingActivationHelper() {
        return new NeoForgeKeyMappingActivationHelper();
    }
}