package fuzs.puzzleslib.impl.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class CopyComponentsShapelessRecipe extends ShapelessRecipe implements CopyComponentsRecipe {
    private final RecipeSerializer<?> recipeSerializer;
    private final Ingredient copyFrom;

    public CopyComponentsShapelessRecipe(String modId, ShapelessRecipe shapelessRecipe, Ingredient copyFrom) {
        this(CopyComponentsRecipe.getModSerializer(modId, CopyComponentsRecipe.SHAPELESS_RECIPE_SERIALIZER_ID), shapelessRecipe, copyFrom);
    }

    public CopyComponentsShapelessRecipe(RecipeSerializer<?> recipeSerializer, ShapelessRecipe shapelessRecipe, Ingredient copyFrom) {
        super(shapelessRecipe.getGroup(), shapelessRecipe.category(), shapelessRecipe.getResultItem(RegistryAccess.EMPTY), shapelessRecipe.getIngredients());
        this.recipeSerializer = recipeSerializer;
        this.copyFrom = copyFrom;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        ItemStack itemStack = super.assemble(craftingInput, registries);
        CopyComponentsRecipe.super.copyComponentsToResult(itemStack, craftingInput);
        return itemStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.recipeSerializer;
    }

    @Override
    public Ingredient getComponentsSource() {
        return this.copyFrom;
    }
}
