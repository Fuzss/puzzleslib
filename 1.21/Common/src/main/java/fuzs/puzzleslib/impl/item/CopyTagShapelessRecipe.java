package fuzs.puzzleslib.impl.item;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class CopyTagShapelessRecipe extends ShapelessRecipe implements CopyTagRecipe {
    private final RecipeSerializer<?> recipeSerializer;
    private final Ingredient copyFrom;

    public CopyTagShapelessRecipe(String modId, ShapelessRecipe shapelessRecipe, Ingredient copyFrom) {
        this(CopyTagRecipe.getModSerializer(modId, CopyTagRecipe.SHAPELESS_RECIPE_SERIALIZER_ID), shapelessRecipe, copyFrom);
    }

    public CopyTagShapelessRecipe(RecipeSerializer<?> recipeSerializer, ShapelessRecipe shapelessRecipe, Ingredient copyFrom) {
        super(shapelessRecipe.getGroup(), shapelessRecipe.category(), shapelessRecipe.getResultItem(RegistryAccess.EMPTY), shapelessRecipe.getIngredients());
        this.recipeSerializer = recipeSerializer;
        this.copyFrom = copyFrom;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        ItemStack itemStack = super.assemble(craftingContainer, registryAccess);
        CopyTagRecipe.super.tryCopyTagToResult(itemStack, craftingContainer);
        return itemStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.recipeSerializer;
    }

    @Override
    public Ingredient getCopyTagSource() {
        return this.copyFrom;
    }
}
