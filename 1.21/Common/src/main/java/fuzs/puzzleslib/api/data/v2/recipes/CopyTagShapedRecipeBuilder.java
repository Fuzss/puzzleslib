package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.impl.item.CopyComponentsRecipe;
import fuzs.puzzleslib.impl.item.CopyComponentsShapedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

// TODO rename to CopyComponentsShapedRecipeBuilder
public class CopyTagShapedRecipeBuilder extends ShapedRecipeBuilder {
    private Ingredient copyFrom;
    
    public CopyTagShapedRecipeBuilder(RecipeCategory recipeCategory, ItemLike result, int count) {
        super(recipeCategory, result, count);
    }

    public static CopyTagShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
        return shaped(category, result, 1);
    }

    public static CopyTagShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
        return new CopyTagShapedRecipeBuilder(category, result, count);
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
    public CopyTagShapedRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        super.unlockedBy(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder group(@Nullable String groupName) {
        super.group(groupName);
        return this;
    }

    @Override
    public CopyTagShapedRecipeBuilder showNotification(boolean bl) {
        super.showNotification(bl);
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
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        super.save(new RecipeOutput() {

            @Override
            public Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }

            @Override
            public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                // some weird hack to get the proper mod id for the serializer
                String modId = recipeOutput instanceof AbstractRecipeProvider.IdentifiableRecipeOutput identifiableRecipeOutput ? identifiableRecipeOutput.getModId() : id.getNamespace();
                RecipeSerializer<?> recipeSerializer = CopyComponentsRecipe.getModSerializer(modId, CopyComponentsRecipe.SHAPED_RECIPE_SERIALIZER_ID);
                recipe = new CopyComponentsShapedRecipe(recipeSerializer, (ShapedRecipe) recipe, CopyTagShapedRecipeBuilder.this.copyFrom);
                recipeOutput.accept(location, recipe, advancement);
            }
        }, id);
    }
}
