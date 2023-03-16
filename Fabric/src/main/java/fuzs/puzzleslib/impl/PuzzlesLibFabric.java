package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.core.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class PuzzlesLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLib::new);
    }
}
