package fuzs.puzzleslib.fabric.impl.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.common.impl.PuzzlesLibMod;
import fuzs.puzzleslib.common.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.common.impl.content.client.PuzzlesLibClientDevelopment;
import net.fabricmc.api.ClientModInitializer;

public class PuzzlesLibFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ClientModConstructor.construct(PuzzlesLibMod.id("client/development"), PuzzlesLibClientDevelopment::new);
        }
    }
}
