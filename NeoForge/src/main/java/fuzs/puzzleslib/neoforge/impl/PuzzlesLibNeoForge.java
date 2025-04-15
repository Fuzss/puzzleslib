package fuzs.puzzleslib.neoforge.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.content.PuzzlesLibDevelopment;
import net.neoforged.fml.common.Mod;

@Mod(PuzzlesLib.MOD_ID)
public class PuzzlesLibNeoForge {

    public PuzzlesLibNeoForge() {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ModConstructor.construct(PuzzlesLibMod.id("development"), PuzzlesLibDevelopment::new);
        }
    }
}
