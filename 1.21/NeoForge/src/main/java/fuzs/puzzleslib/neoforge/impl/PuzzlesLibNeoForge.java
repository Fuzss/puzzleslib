package fuzs.puzzleslib.neoforge.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(PuzzlesLib.MOD_ID)
public class PuzzlesLibNeoForge {

    public PuzzlesLibNeoForge(ModContainer modContainer) {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
    }
}
