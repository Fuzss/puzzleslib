package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import fuzs.puzzleslib.impl.item.TransmuteShapelessRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TransmuteShapelessRecipeBuilder extends ShapelessRecipeBuilder {
    private final RecipeSerializer<?> recipeSerializer;
    private Ingredient input;

    public TransmuteShapelessRecipeBuilder(RecipeSerializer<?> recipeSerializer, RecipeCategory recipeCategory, ItemStack result) {
        super(recipeCategory, result.getItem(), result.getCount());
        this.recipeSerializer = recipeSerializer;
    }

    public static TransmuteShapelessRecipeBuilder shapeless(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result) {
        return shapeless(recipeSerializer, category, result, 1);
    }

    public static TransmuteShapelessRecipeBuilder shapeless(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result, int count) {
        return new TransmuteShapelessRecipeBuilder(recipeSerializer,
                category,
                result.asItem().getDefaultInstance().copyWithCount(count));
    }

    public static RecipeSerializer<?> getRecipeSerializer(String modId) {
        return CustomTransmuteRecipe.getModSerializer(modId,
                CustomTransmuteRecipe.TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID);
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(TagKey<Item> tag) {
        super.requires(tag);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(ItemLike item) {
        super.requires(item);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        super.requires(item, quantity);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient) {
        super.requires(ingredient);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
        super.requires(ingredient, quantity);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        super.unlockedBy(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder group(@Nullable String groupName) {
        super.group(groupName);
        return this;
    }

    public TransmuteShapelessRecipeBuilder input(ItemLike input) {
        return this.input(Ingredient.of(input));
    }

    public TransmuteShapelessRecipeBuilder input(Ingredient input) {
        Objects.requireNonNull(input, "input is null");
        this.input = input;
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        Objects.requireNonNull(this.input, "input is null");
        super.save(new TransformingRecipeOutput(recipeOutput, (Recipe<?> recipe) -> {
            return new TransmuteShapelessRecipe(TransmuteShapelessRecipeBuilder.this.recipeSerializer,
                    (ShapelessRecipe) recipe,
                    TransmuteShapelessRecipeBuilder.this.input);
        }), resourceLocation);
    }
}
