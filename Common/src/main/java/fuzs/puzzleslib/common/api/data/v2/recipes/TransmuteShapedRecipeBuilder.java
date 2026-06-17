package fuzs.puzzleslib.common.api.data.v2.recipes;

import fuzs.puzzleslib.common.impl.item.TransmuteShapedRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class TransmuteShapedRecipeBuilder extends ShapedRecipeBuilder {
    private final ResourceKey<RecipeSerializer<?>> serializerKey;
    private Ingredient input;

    public TransmuteShapedRecipeBuilder(ResourceKey<RecipeSerializer<?>> serializerKey, HolderGetter<Item> holderGetter, RecipeCategory category, ItemStackTemplate result) {
        super(holderGetter, category, result);
        this.serializerKey = serializerKey;
    }

    public static TransmuteShapedRecipeBuilder shaped(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result) {
        return shaped(recipeSerializer, holderGetter, category, result, 1);
    }

    public static TransmuteShapedRecipeBuilder shaped(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result, int count) {
        return new TransmuteShapedRecipeBuilder(recipeSerializer,
                holderGetter,
                category,
                new ItemStackTemplate(result.asItem(), count));
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
    public TransmuteShapedRecipeBuilder pattern(String row) {
        super.pattern(row);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        super.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder group(@Nullable String group) {
        super.group(group);
        return this;
    }

    @Override
    public TransmuteShapedRecipeBuilder showNotification(boolean showNotification) {
        super.showNotification(showNotification);
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
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Objects.requireNonNull(this.input, "input is null");
        super.save(TransformingRecipeOutput.transformed(output, (Recipe<?> recipe) -> {
            return new TransmuteShapedRecipe(TransmuteShapedRecipeBuilder.this.serializerKey,
                    (ShapedRecipe) recipe,
                    TransmuteShapedRecipeBuilder.this.input);
        }), id);
    }
}
