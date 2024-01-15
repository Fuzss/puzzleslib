package fuzs.puzzleslib.neoforge.api.data.v2.recipes;

import fuzs.puzzleslib.impl.item.CopyTagRecipe;
import fuzs.puzzleslib.impl.item.CopyTagShapelessRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class CopyTagShapelessRecipeBuilder extends ShapelessRecipeBuilder {
    private Ingredient copyFrom;

    public CopyTagShapelessRecipeBuilder(RecipeCategory recipeCategory, ItemLike result, int count) {
        super(recipeCategory, result, count);
    }

    public static CopyTagShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result) {
        return shapeless(category, result, 1);
    }

    public static CopyTagShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count) {
        return new CopyTagShapelessRecipeBuilder(category, result, count);
    }

    @Override
    public CopyTagShapelessRecipeBuilder requires(TagKey<Item> tag) {
        super.requires(tag);
        return this;
    }

    @Override
    public CopyTagShapelessRecipeBuilder requires(ItemLike item) {
        super.requires(item);
        return this;
    }

    @Override
    public CopyTagShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        super.requires(item, quantity);
        return this;
    }

    @Override
    public CopyTagShapelessRecipeBuilder requires(Ingredient ingredient) {
        super.requires(ingredient);
        return this;
    }

    @Override
    public CopyTagShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
        super.requires(ingredient, quantity);
        return this;
    }

    @Override
    public CopyTagShapelessRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        super.unlockedBy(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public CopyTagShapelessRecipeBuilder group(@Nullable String groupName) {
        super.group(groupName);
        return this;
    }

    public CopyTagShapelessRecipeBuilder copyFrom(ItemLike copyFrom) {
        return this.copyFrom(Ingredient.of(copyFrom));
    }

    public CopyTagShapelessRecipeBuilder copyFrom(Ingredient copyFrom) {
        this.copyFrom = copyFrom;
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        super.save(new RecipeOutput() {

            @Override
            public Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }

            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                RecipeSerializer<?> recipeSerializer = CopyTagRecipe.getModSerializer(resourceLocation.getNamespace(), CopyTagRecipe.SHAPELESS_RECIPE_SERIALIZER_ID);
                recipe = new CopyTagShapelessRecipe(recipeSerializer, (ShapelessRecipe) recipe, CopyTagShapelessRecipeBuilder.this.copyFrom);
                recipeOutput.accept(id, recipe, advancement, conditions);
            }
        }, resourceLocation);
    }
}
