package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.core.CoreServices;
import net.fabricmc.api.ModInitializer;

public class PuzzlesLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(PuzzlesLib.MOD_ID).accept(new PuzzlesLib());
    }
}
