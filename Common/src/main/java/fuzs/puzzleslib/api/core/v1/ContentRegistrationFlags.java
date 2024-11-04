package fuzs.puzzleslib.api.core.v1;

import org.jetbrains.annotations.ApiStatus;

/**
 * Allows for specifying certain mod contents that need something to be registered to enable the mod loader specific
 * implementation to work (mostly intended for Forge).
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
     * Registers a {@link net.minecraft.world.item.crafting.RecipeSerializer} for custom crafting recipes that support
     * copying the full item nbt tag of a single ingredient item to the result item.
     * <p>
     * Intended to be used for upgrading items to a higher tier, similar to the smithing table.
     */
    COPY_RECIPES;

    @ApiStatus.Internal
    public static void throwForFlag(ContentRegistrationFlags flag) {
        throw new IllegalStateException(
                "%s#%s is missing".formatted(ContentRegistrationFlags.class.getSimpleName(), flag));
    }
}
