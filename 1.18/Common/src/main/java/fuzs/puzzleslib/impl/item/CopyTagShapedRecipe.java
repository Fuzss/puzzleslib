package fuzs.puzzleslib.impl.item;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CopyTagShapedRecipe extends ShapedRecipe implements CopyTagRecipe {
    private final Ingredient copyFrom;

    public CopyTagShapedRecipe(ShapedRecipe shapedRecipe, Ingredient copyFrom) {
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getWidth(), shapedRecipe.getHeight(), shapedRecipe.getIngredients(), shapedRecipe.getResultItem());
        this.copyFrom = copyFrom;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack itemStack = super.assemble(craftingContainer);
        CopyTagRecipe.super.tryCopyTagToResult(itemStack, craftingContainer);
        return itemStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CopyTagRecipe.getModSerializer(this.getId().getNamespace(), CopyTagRecipe.SHAPED_RECIPE_SERIALIZER_ID);
    }

    @Override
    public Ingredient getCopyTagSource() {
        return this.copyFrom;
    }
}
