package fuzs.puzzleslib.impl.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class TransmuteShapedRecipe extends ShapedRecipe implements CustomTransmuteRecipe {
    private final RecipeSerializer<?> recipeSerializer;
    private final Ingredient input;

    public TransmuteShapedRecipe(String modId, ShapedRecipe shapedRecipe, Ingredient input) {
        this(CustomTransmuteRecipe.getModSerializer(modId, CustomTransmuteRecipe.TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID),
                shapedRecipe,
                input);
    }

    public TransmuteShapedRecipe(RecipeSerializer<?> recipeSerializer, ShapedRecipe shapedRecipe, Ingredient input) {
        super(shapedRecipe.group(),
                shapedRecipe.category(),
                shapedRecipe.pattern,
                shapedRecipe.result,
                shapedRecipe.showNotification());
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
    public RecipeSerializer<? extends ShapedRecipe> getSerializer() {
        return (RecipeSerializer<? extends ShapedRecipe>) this.recipeSerializer;
    }

    @Override
    public Ingredient getInput() {
        return this.input;
    }
}
