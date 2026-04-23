package fuzs.puzzleslib.impl.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.Objects;

public final class TransmuteShapelessRecipe extends ShapelessRecipe implements CustomTransmuteRecipe {
    private final ResourceKey<RecipeSerializer<?>> serializerKey;
    private final Ingredient input;

    public TransmuteShapelessRecipe(ResourceKey<RecipeSerializer<?>> serializerKey, ShapelessRecipe shapelessRecipe, Ingredient input) {
        super(shapelessRecipe.commonInfo,
                shapelessRecipe.bookInfo,
                shapelessRecipe.result,
                shapelessRecipe.ingredients);
        this.serializerKey = serializerKey;
        this.input = input;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput) {
        ItemStack itemStack = super.assemble(craftingInput);
        CustomTransmuteRecipe.super.transmuteInput(itemStack, craftingInput);
        return itemStack;
    }

    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        RecipeSerializer<?> recipeSerializer = BuiltInRegistries.RECIPE_SERIALIZER.getValue(this.serializerKey);
        Objects.requireNonNull(recipeSerializer, () -> "recipe serializer '" + this.serializerKey + "' not registered");
        return (RecipeSerializer<ShapelessRecipe>) recipeSerializer;
    }

    @Override
    public Ingredient getInput() {
        return this.input;
    }
}
