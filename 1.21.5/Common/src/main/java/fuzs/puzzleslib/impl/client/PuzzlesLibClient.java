package fuzs.puzzleslib.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.AddResourcePackReloadListenersCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.impl.client.config.ConfigTranslationsManager;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public class PuzzlesLibClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerLoadingHandlers();
    }

    private static void registerLoadingHandlers() {
        AddResourcePackReloadListenersCallback.EVENT.register(ConfigTranslationsManager::onAddResourcePackReloadListeners);
    }

    @Override
    public void onClientSetup() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientPlayerNetworkEvents.LOGGED_OUT.register((LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) -> {
            ModContext.clearPresentServerside();
        });
    }
}
