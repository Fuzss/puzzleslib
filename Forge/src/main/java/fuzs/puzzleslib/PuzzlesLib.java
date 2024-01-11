package fuzs.puzzleslib;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility class for common helper methods
 * also main puzzles lib mod, only really need, so it shows in the mods list
 */
@Deprecated(forRemoval = true)
public class PuzzlesLib {
    /**
     * puzzles lib mod id
     */
    public static final String MOD_ID = "puzzleslib";
    /**
     * puzzles lib mod name
     */
    public static final String MOD_NAME = "Puzzles Lib";
    /**
     * puzzles lib logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(PuzzlesLib.MOD_NAME);

    /**
     * set mod to only be required on one side, server or client
     * works like <code>clientSideOnly</code> back in 1.12
     */
    public static void setSideOnly() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, server) -> true));
    }
}
