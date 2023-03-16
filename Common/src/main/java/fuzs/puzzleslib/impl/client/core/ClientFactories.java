package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.puzzleslib.client.gui.screens.ScreenHelper;
import fuzs.puzzleslib.core.ContentRegistrationFlags;
import fuzs.puzzleslib.util.PuzzlesUtil;

import java.util.function.Supplier;

public interface ClientFactories {
    ClientFactories INSTANCE = PuzzlesUtil.loadServiceProvider(ClientFactories.class);

    void constructClientMod(String modId, Supplier<ClientModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations);

    ScreenHelper getScreenHelper();
}
