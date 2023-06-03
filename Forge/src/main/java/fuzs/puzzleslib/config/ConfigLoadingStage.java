package fuzs.puzzleslib.config;

/**
 * used for tracking config load process, only state we really care about is {@link #AVAILABLE}
 */
enum ConfigLoadingStage {
    /**
     * config does not exist within this mod context, meaning the {@link AbstractConfig} object is null
     */
    NOT_PRESENT,
    /**
     * config exists, but corresponding {@link net.minecraftforge.fml.config.ModConfig} has not yet been created
     */
    INITIALIZED,
    /**
     * {@link net.minecraftforge.fml.config.ModConfig} is present, but config data within it is not loaded
     *
     * for server configs after a world save is unloaded {@link #AVAILABLE} should be returned to this
     */
    MISSING_DATA,
    /**
     * config data is present in {@link net.minecraftforge.fml.config.ModConfig}, but our callbacks have not been loaded,
     * therefore config values are not safe to use yet
     */
    LOADED,
    /**
     * the config is fully loaded and ready to use
     */
    AVAILABLE
}
