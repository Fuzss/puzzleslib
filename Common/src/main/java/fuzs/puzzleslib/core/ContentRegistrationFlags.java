package fuzs.puzzleslib.core;

/**
 * Allows for specifying certain mod contents that need something to be registered to enable the mod loader specific implementation to work (mostly on Forge)
 */
public enum ContentRegistrationFlags {
    /**
     * Registers a biome modifier, and it's codec on Forge to allow the custom biome modification system to work
     */
    BIOMES,
    /**
     * Register a client-side resource reload listener that reloads built-in item model renderers
     */
    BUILT_IN_ITEM_MODEL_RENDERERS
}
