package fuzs.puzzleslib.impl.item;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class CopyTagShapelessRecipe extends ShapelessRecipe implements CopyTagRecipe {
    private final Ingredient copyFrom;

    public CopyTagShapelessRecipe(ShapelessRecipe other, Ingredient copyFrom) {
        super(other.getId(), other.getGroup(), other.category(), other.getResultItem(RegistryAccess.EMPTY), other.getIngredients());
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
        return CopyTagRecipe.getModSerializer(this.getId().getNamespace(), CopyTagRecipe.SHAPELESS_RECIPE_SERIALIZER_ID);
    }

    @Override
    public Ingredient getCopyTagSource() {
        return this.copyFrom;
    }
}
