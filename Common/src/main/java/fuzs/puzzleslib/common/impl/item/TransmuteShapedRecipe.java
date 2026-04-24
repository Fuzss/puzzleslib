package fuzs.puzzleslib.common.impl.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Objects;

public final class TransmuteShapedRecipe extends ShapedRecipe implements CustomTransmuteRecipe {
    private final ResourceKey<RecipeSerializer<?>> serializerKey;
    private final Ingredient input;

    public TransmuteShapedRecipe(ResourceKey<RecipeSerializer<?>> serializerKey, ShapedRecipe shapedRecipe, Ingredient input) {
        super(shapedRecipe.commonInfo, shapedRecipe.bookInfo, shapedRecipe.pattern, shapedRecipe.result);
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
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        RecipeSerializer<?> recipeSerializer = BuiltInRegistries.RECIPE_SERIALIZER.getValue(this.serializerKey);
        Objects.requireNonNull(recipeSerializer, () -> "recipe serializer '" + this.serializerKey + "' not registered");
        return (RecipeSerializer<ShapedRecipe>) recipeSerializer;
    }

    @Override
    public Ingredient getInput() {
        return this.input;
    }
}
