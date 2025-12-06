package fuzs.puzzleslib.impl.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

@Deprecated
public class CopyComponentsShapedRecipe extends ShapedRecipe implements CopyComponentsRecipe {
    private final RecipeSerializer<?> recipeSerializer;
    private final Ingredient copyFrom;

    public CopyComponentsShapedRecipe(String modId, ShapedRecipe shapedRecipe, Ingredient copyFrom) {
        this(CopyComponentsRecipe.getModSerializer(modId, CopyComponentsRecipe.SHAPED_RECIPE_SERIALIZER_ID),
                shapedRecipe,
                copyFrom);
    }

    public CopyComponentsShapedRecipe(RecipeSerializer<?> recipeSerializer, ShapedRecipe shapedRecipe, Ingredient copyFrom) {
        super(shapedRecipe.getGroup(),
                shapedRecipe.category(),
                shapedRecipe.pattern,
                shapedRecipe.getResultItem(RegistryAccess.EMPTY),
                shapedRecipe.showNotification());
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
