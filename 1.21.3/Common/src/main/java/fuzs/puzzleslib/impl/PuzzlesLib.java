package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Deprecated(forRemoval = true)
    public static boolean isDevelopmentEnvironmentWithoutDataGeneration() {
        return ModLoaderEnvironment.INSTANCE.isPuzzlesLibDevelopmentEnvironmentWithoutDataGeneration();
    }

    @Deprecated(forRemoval = true)
    public static boolean isDevelopmentEnvironment() {
        return ModLoaderEnvironment.INSTANCE.isPuzzlesLibDevelopmentEnvironment();
    }
}
