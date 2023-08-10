package fuzs.puzzleslib.impl.item;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiFunction;

public record FabricCopyTagRecipeSerializer<T extends CraftingRecipe, S extends CraftingRecipe & CopyTagRecipe>(
        RecipeSerializer<T> serializer,
        BiFunction<T, Ingredient, S> factory) implements CopyTagRecipe.Serializer<T, S> {

}
