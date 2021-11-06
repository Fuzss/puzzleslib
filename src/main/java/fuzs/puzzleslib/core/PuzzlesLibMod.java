package fuzs.puzzleslib.core;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * main puzzles lib mod, only really need so it shows in the mods list, this is just a library otherwise
 */
@Mod(PuzzlesLibMod.MOD_ID)
public class PuzzlesLibMod {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LogManager.getLogger(PuzzlesLibMod.MOD_NAME);
}
