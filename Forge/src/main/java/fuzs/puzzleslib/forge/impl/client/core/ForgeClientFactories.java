package fuzs.puzzleslib.forge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingActivationHelper;
import fuzs.puzzleslib.api.client.gui.v2.screen.ScreenHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.forge.impl.client.gui.ForgeScreenHelper;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.forge.impl.client.init.ForgeItemDisplayOverrides;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.forge.impl.client.key.ForgeKeyMappingActivationHelper;

import java.util.Set;

public final class ForgeClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        ForgeClientModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ScreenHelper getScreenHelper() {
        return new ForgeScreenHelper();
    }

    @Override
    public ItemDisplayOverridesImpl getItemModelDisplayOverrides() {
        return new ForgeItemDisplayOverrides();
    }

    @Override
    public KeyMappingActivationHelper getKeyMappingActivationHelper() {
        return new ForgeKeyMappingActivationHelper();
    }
}
