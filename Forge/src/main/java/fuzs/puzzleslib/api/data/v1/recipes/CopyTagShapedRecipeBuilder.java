package fuzs.puzzleslib.api.data.v1.recipes;

import fuzs.puzzleslib.impl.item.CopyTagRecipe;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CopyTagShapedRecipeBuilder extends ShapedRecipeBuilder {
    private Ingredient copyFrom;
    
    public CopyTagShapedRecipeBuilder(ItemLike result, int count) {
        super(result, count);
    }

    public static CopyTagShapedRecipeBuilder shaped(ItemLike result) {
        return shaped(result, 1);
    }

    public static CopyTagShapedRecipeBuilder shaped(ItemLike result, int count) {
        return new CopyTagShapedRecipeBuilder(result, count);
    }

    @Override
    public CopyTagShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        super.define(symbol, tag);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder define(Character symbol, ItemLike item) {
        super.define(symbol, item);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        super.define(symbol, ingredient);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder pattern(String pattern) {
        super.pattern(pattern);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        super.unlockedBy(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder group(@Nullable String groupName) {
        super.group(groupName);
        return this;
    }

    public CopyTagShapedRecipeBuilder copyFrom(ItemLike copyFrom) {
        return this.copyFrom(Ingredient.of(copyFrom));
    }

    public CopyTagShapedRecipeBuilder copyFrom(Ingredient copyFrom) {
        this.copyFrom = copyFrom;
        return this;
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation resourceLocation) {
        super.save(finishedRecipe -> {
            RecipeSerializer<?> recipeSerializer = CopyTagRecipe.getModSerializer(resourceLocation.getNamespace(), CopyTagRecipe.SHAPED_RECIPE_SERIALIZER_ID);
            finishedRecipeConsumer.accept(new ForwardingFinishedRecipe(finishedRecipe, json -> {
                json.add("copy_from", this.copyFrom.toJson());
            }, recipeSerializer));
        }, resourceLocation);
    }
}
