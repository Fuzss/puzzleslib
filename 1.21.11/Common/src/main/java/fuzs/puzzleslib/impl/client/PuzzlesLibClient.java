package fuzs.puzzleslib.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.ResourcePackReloadListenersContext;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.config.ConfigTranslationsManager;

public class PuzzlesLibClient implements ClientModConstructor {

    @Override
    public void onAddResourcePackReloadListeners(ResourcePackReloadListenersContext context) {
        context.registerReloadListener(ResourcePackReloadListenersContext.LANGUAGES,
                PuzzlesLibMod.id("config_translations"),
                ConfigTranslationsManager.INSTANCE);
    }
}
