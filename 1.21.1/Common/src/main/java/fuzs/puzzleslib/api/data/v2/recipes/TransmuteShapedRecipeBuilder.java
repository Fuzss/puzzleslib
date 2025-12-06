package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import fuzs.puzzleslib.impl.item.TransmuteShapedRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TransmuteShapedRecipeBuilder extends ShapedRecipeBuilder {
    private final RecipeSerializer<?> recipeSerializer;
    private Ingredient input;

    public TransmuteShapedRecipeBuilder(RecipeSerializer<?> recipeSerializer, RecipeCategory recipeCategory, ItemStack result) {
        super(recipeCategory, result.getItem(), result.getCount());
        this.recipeSerializer = recipeSerializer;
    }

    public static TransmuteShapedRecipeBuilder shaped(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result) {
        return shaped(recipeSerializer, category, result, 1);
    }

    public static TransmuteShapedRecipeBuilder shaped(RecipeSerializer<?> recipeSerializer, RecipeCategory category, ItemLike result, int count) {
        return new TransmuteShapedRecipeBuilder(recipeSerializer, category,
                result.asItem().getDefaultInstance().copyWithCount(count));
    }

    public static RecipeSerializer<?> getRecipeSerializer(String modId) {
        return CustomTransmuteRecipe.getModSerializer(modId,
                CustomTransmuteRecipe.TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID);
    }

    @Override
    public TransmuteShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        super.define(symbol, tag);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder define(Character symbol, ItemLike item) {
        super.define(symbol, item);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        super.define(symbol, ingredient);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder pattern(String pattern) {
        super.pattern(pattern);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        super.unlockedBy(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder group(@Nullable String groupName) {
        super.group(groupName);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder showNotification(boolean bl) {
        super.showNotification(bl);
        return this;
    }

    public TransmuteShapedRecipeBuilder input(ItemLike input) {
        return this.input(Ingredient.of(input));
    }

    public TransmuteShapedRecipeBuilder input(Ingredient input) {
        Objects.requireNonNull(input, "input is null");
        this.input = input;
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        Objects.requireNonNull(this.input, "input is null");
        super.save(new TransformingRecipeOutput(recipeOutput, (Recipe<?> recipe) -> {
            return new TransmuteShapedRecipe(TransmuteShapedRecipeBuilder.this.recipeSerializer,
                    (ShapedRecipe) recipe,
                    TransmuteShapedRecipeBuilder.this.input);
        }), resourceLocation);
    }
}
