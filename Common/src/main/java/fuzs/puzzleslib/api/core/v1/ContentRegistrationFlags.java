package fuzs.puzzleslib.api.core.v1;

import org.jetbrains.annotations.ApiStatus;

/**
 * Allows for specifying certain mod contents that need something to be registered to enable the implementation to
 * work.
 */
public enum ContentRegistrationFlags {
    /**
     * Registers a biome modifier, and it's codec on NeoForge to allow the custom biome modification system to work.
     */
    BIOME_MODIFICATIONS,
    /**
     * Register a client-side resource reload listener that reloads built-in item model renderers.
     */
    DYNAMIC_RENDERERS,
    /**
     * Registers {@link net.minecraft.world.item.crafting.RecipeSerializer RecipeSerializers} for transmutation crafting
     * recipes that support preserving all item stack components.
     * <p>
     * Intended to be used for upgrading items to a higher tier, similar to the smithing table.
     */
    CRAFTING_TRANSMUTE;

    @ApiStatus.Internal
    public static void throwForFlag(ContentRegistrationFlags flag) {
        throw new IllegalStateException(ContentRegistrationFlags.class.getSimpleName() + "#" + flag + " is missing");
    }
}
