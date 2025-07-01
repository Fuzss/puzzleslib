package fuzs.puzzleslib.api.data.v2.recipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

/**
 * Allows for using vanilla recipe builders with custom recipe implementations based on vanilla recipe types.
 */
public record TransformingRecipeOutput(RecipeOutput recipeOutput,
                                       UnaryOperator<Recipe<?>> operator) implements RecipeOutput {

    @Override
    public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
        this.recipeOutput.accept(key, this.operator().apply(recipe), advancement);
    }

    @Override
    public Advancement.Builder advancement() {
        return this.recipeOutput.advancement();
    }

    @Override
    public void includeRootAdvancement() {
        this.recipeOutput.includeRootAdvancement();
    }
}
