package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static boolean isDevelopmentEnvironmentWithoutDataGeneration() {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            return false;
        } else {
            return isDevelopmentEnvironment();
        }
    }

    public static boolean isDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) {
            return false;
        } else {
            return Boolean.getBoolean(MOD_ID + ".isDevelopmentEnvironment");
        }
    }
}
