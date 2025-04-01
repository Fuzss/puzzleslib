package fuzs.puzzleslib.fabric.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.content.PuzzlesLibDevelopment;
import net.fabricmc.api.ModInitializer;

public class PuzzlesLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibDevelopment::new);
        }
    }
}
