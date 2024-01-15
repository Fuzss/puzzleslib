package fuzs.puzzleslib.neoforge.impl.item.crafting;

import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.NBTIngredient;

import java.util.Objects;

public final class NeoForgeCombinedIngredients implements CombinedIngredients {

    @Override
    public Ingredient all(Ingredient... ingredients) {
        Objects.requireNonNull(ingredients, "ingredients is null");
        for (Ingredient ingredient : ingredients) Objects.requireNonNull(ingredient, "ingredient is null");
        return IntersectionIngredient.of(ingredients);
    }

    @Override
    public Ingredient any(Ingredient... ingredients) {
        Objects.requireNonNull(ingredients, "ingredients is null");
        for (Ingredient ingredient : ingredients) Objects.requireNonNull(ingredient, "ingredient is null");
        return CompoundIngredient.of(ingredients);
    }

    @Override
    public Ingredient difference(Ingredient ingredient, Ingredient subtracted) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(subtracted, "subtracted is null");
        return DifferenceIngredient.of(ingredient, subtracted);
    }

    @Override
    public Ingredient nbt(ItemStack stack, boolean strict) {
        Objects.requireNonNull(stack, "stack is null");
        if (strict) {
            return NBTIngredient.of(true, stack);
        } else {
            CompoundTag tag = stack.getTag();
            Objects.requireNonNull(tag, "tag is null");
            return NBTIngredient.of(false, tag, stack.getItem());
        }
    }
}
