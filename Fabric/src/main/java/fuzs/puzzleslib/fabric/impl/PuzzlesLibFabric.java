package fuzs.puzzleslib.fabric.impl;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.common.impl.PuzzlesLibMod;
import fuzs.puzzleslib.common.impl.content.PuzzlesLibDevelopment;
import fuzs.puzzleslib.common.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import net.fabricmc.api.ModInitializer;

public class PuzzlesLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ModConstructorImpl.construct(PuzzlesLibMod.id("common/development"),
                    PuzzlesLibDevelopment::new,
                    ProxyImpl.get()::getModConstructorImpl);
        }
    }
}
