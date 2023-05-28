package fuzs.puzzleslib.api.core.v1;

/**
 * Allows for specifying certain mod contents that need something to be registered to enable the mod loader specific implementation to work (mostly intended for Forge).
 */
public enum ContentRegistrationFlags {
    /**
     * Registers a biome modifier, and it's codec on Forge to allow the custom biome modification system to work.
     */
    BIOME_MODIFICATIONS,
    /**
     * Register a client-side resource reload listener that reloads built-in item model renderers.
     */
    DYNAMIC_RENDERERS,
    /**
     * Registers a {@link net.minecraft.world.item.crafting.RecipeSerializer} for a recipe of type {@link fuzs.puzzleslib.api.item.v2.LegacySmithingTransformRecipe}
     * for upgrading gear without the need for a smithing template, just like the old smithing used to work.
     */
    LEGACY_SMITHING
}
