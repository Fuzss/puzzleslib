package fuzs.puzzleslib.fabric.impl.item.crafting;

import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public final class FabricCombinedIngredients implements CombinedIngredients {

    @Override
    public Ingredient all(Ingredient... ingredients) {
        Objects.requireNonNull(ingredients, "ingredients is null");
        for (Ingredient ingredient : ingredients) Objects.requireNonNull(ingredient, "ingredient is null");
        return DefaultCustomIngredients.all(ingredients);
    }

    @Override
    public Ingredient any(Ingredient... ingredients) {
        Objects.requireNonNull(ingredients, "ingredients is null");
        for (Ingredient ingredient : ingredients) Objects.requireNonNull(ingredient, "ingredient is null");
        return DefaultCustomIngredients.any(ingredients);
    }

    @Override
    public Ingredient difference(Ingredient ingredient, Ingredient subtracted) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(subtracted, "subtracted is null");
        return DefaultCustomIngredients.difference(ingredient, subtracted);
    }

    @Override
    public Ingredient nbt(ItemStack stack, boolean strict) {
        Objects.requireNonNull(stack, "stack is null");
        if (!strict) Objects.requireNonNull(stack.getTag(), "tag is null");
        return DefaultCustomIngredients.components(stack, strict);
    }
}
