package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.impl.item.CopyComponentsRecipe;
import fuzs.puzzleslib.impl.item.CopyComponentsShapelessRecipe;
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
import org.jetbrains.annotations.Nullable;

public class CopyComponentsShapelessRecipeBuilder extends ShapelessRecipeBuilder {
    private Ingredient copyFrom;

    public CopyComponentsShapelessRecipeBuilder(RecipeCategory recipeCategory, ItemLike result, int count) {
        super(recipeCategory, result, count);
    }

    public static CopyComponentsShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result) {
        return shapeless(category, result, 1);
    }

    public static CopyComponentsShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count) {
        return new CopyComponentsShapelessRecipeBuilder(category, result, count);
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder requires(TagKey<Item> tag) {
        super.requires(tag);
        return this;
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder requires(ItemLike item) {
        super.requires(item);
        return this;
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        super.requires(item, quantity);
        return this;
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder requires(Ingredient ingredient) {
        super.requires(ingredient);
        return this;
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
        super.requires(ingredient, quantity);
        return this;
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        super.unlockedBy(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public CopyComponentsShapelessRecipeBuilder group(@Nullable String groupName) {
        super.group(groupName);
        return this;
    }

    public CopyComponentsShapelessRecipeBuilder copyFrom(ItemLike copyFrom) {
        return this.copyFrom(Ingredient.of(copyFrom));
    }

    public CopyComponentsShapelessRecipeBuilder copyFrom(Ingredient copyFrom) {
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
                String modId =
                        recipeOutput instanceof AbstractRecipeProvider.IdentifiableRecipeOutput identifiableRecipeOutput ?
                                identifiableRecipeOutput.getModId() : id.getNamespace();
                RecipeSerializer<?> recipeSerializer = CopyComponentsRecipe.getModSerializer(modId,
                        CopyComponentsRecipe.SHAPELESS_RECIPE_SERIALIZER_ID
                );
                recipe = new CopyComponentsShapelessRecipe(recipeSerializer, (ShapelessRecipe) recipe,
                        CopyComponentsShapelessRecipeBuilder.this.copyFrom
                );
                recipeOutput.accept(location, recipe, advancement);
            }
        }, id);
    }
}
