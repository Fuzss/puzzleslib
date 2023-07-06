package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class PuzzlesLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(PuzzlesLib.MOD_ID).accept(new PuzzlesLib());
    }
}
