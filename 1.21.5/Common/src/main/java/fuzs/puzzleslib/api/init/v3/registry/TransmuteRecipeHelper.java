package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

/**
 * Contains methods for registering our custom transmute recipe implementations.
 */
public final class TransmuteRecipeHelper {

    private TransmuteRecipeHelper() {
        // NO-OP
    }

    /**
     * Registers mod-specific recipe serializers for custom transmute recipes.
     *
     * @param registryManager the registry manager instance
     */
    public static void registerTransmuteRecipeSerializers(RegistryManager registryManager) {
        CustomTransmuteRecipe.registerSerializers((String string, Supplier<RecipeSerializer<?>> recipeSerializerSupplier) -> {
            registryManager.register(Registries.RECIPE_SERIALIZER, string, recipeSerializerSupplier);
        });
    }
}
