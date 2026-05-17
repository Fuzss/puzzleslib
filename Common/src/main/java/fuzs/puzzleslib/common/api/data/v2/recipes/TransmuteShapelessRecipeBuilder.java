package fuzs.puzzleslib.common.api.data.v2.recipes;

import fuzs.puzzleslib.common.impl.item.TransmuteShapelessRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class TransmuteShapelessRecipeBuilder extends ShapelessRecipeBuilder {
    private final ResourceKey<RecipeSerializer<?>> serializerKey;
    private Ingredient input;

    public TransmuteShapelessRecipeBuilder(ResourceKey<RecipeSerializer<?>> serializerKey, HolderGetter<Item> holderGetter, RecipeCategory category, ItemStackTemplate result) {
        super(holderGetter, category, result);
        this.serializerKey = serializerKey;
    }

    public static TransmuteShapelessRecipeBuilder shapeless(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result) {
        return shapeless(recipeSerializer, holderGetter, category, result, 1);
    }

    public static TransmuteShapelessRecipeBuilder shapeless(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result, int count) {
        return new TransmuteShapelessRecipeBuilder(recipeSerializer,
                holderGetter,
                category,
                new ItemStackTemplate(result.asItem(), count));
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
    public TransmuteShapelessRecipeBuilder requires(ItemLike item, int count) {
        super.requires(item, count);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient) {
        super.requires(ingredient);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient, int count) {
        super.requires(ingredient, count);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        super.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public TransmuteShapelessRecipeBuilder group(@Nullable String group) {
        super.group(group);
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
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Objects.requireNonNull(this.input, "input is null");
        super.save(TransformingRecipeOutput.transformed(output, (Recipe<?> recipe) -> {
            return new TransmuteShapelessRecipe(TransmuteShapelessRecipeBuilder.this.serializerKey,
                    (ShapelessRecipe) recipe,
                    TransmuteShapelessRecipeBuilder.this.input);
        }), id);
    }
}
