package fuzs.puzzleslib.impl.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class TransmuteShapelessRecipe extends ShapelessRecipe implements CustomTransmuteRecipe {
    private final RecipeSerializer<?> recipeSerializer;
    private final Ingredient input;

    public TransmuteShapelessRecipe(String modId, ShapelessRecipe shapelessRecipe, Ingredient input) {
        this(CustomTransmuteRecipe.getModSerializer(modId,
                CustomTransmuteRecipe.TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID), shapelessRecipe, input);
    }

    public TransmuteShapelessRecipe(RecipeSerializer<?> recipeSerializer, ShapelessRecipe shapelessRecipe, Ingredient input) {
        super(shapelessRecipe.getGroup(),
                shapelessRecipe.category(),
                shapelessRecipe.getResultItem(RegistryAccess.EMPTY),
                shapelessRecipe.getIngredients());
        this.recipeSerializer = recipeSerializer;
        this.input = input;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        ItemStack itemStack = super.assemble(craftingInput, registries);
        CustomTransmuteRecipe.super.transmuteInput(itemStack, craftingInput);
        return itemStack;
    }

    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer<ShapelessRecipe>) this.recipeSerializer;
    }

    @Override
    public Ingredient getInput() {
        return this.input;
    }
}
