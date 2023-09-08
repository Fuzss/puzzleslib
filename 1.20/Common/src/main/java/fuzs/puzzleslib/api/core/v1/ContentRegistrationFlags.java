package fuzs.puzzleslib.api.core.v1;

import org.jetbrains.annotations.ApiStatus;

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
     *
     * @deprecated replaced by {@link #COPY_TAG_RECIPES}
     */
    @Deprecated(forRemoval = true)
    LEGACY_SMITHING,
    /**
     * Registers a {@link net.minecraft.world.item.crafting.RecipeSerializer} for custom crafting recipes that support copying the full item nbt tag of a single ingredient item to the result item.
     * <p>Intended to be used for upgrading items to a higher tier, similar to the smithing table.
     */
    COPY_TAG_RECIPES,
    /**
     * A fully client-side particle factory registration helper for particle types that have not been registered in common and will not be present on dedicated servers.
     * <p>Intended for client-only mods to allow for custom particles, which would otherwise be unavailable playing on dedicated servers if registered normally due to registry sync (at least on Forge).
     */
    CLIENT_PARTICLE_TYPES;

    @ApiStatus.Internal
    public static void throwForFlag(ContentRegistrationFlags flag) {
        throw new IllegalStateException("%s#%s is missing".formatted(ContentRegistrationFlags.class.getSimpleName(), flag));
    }
}
