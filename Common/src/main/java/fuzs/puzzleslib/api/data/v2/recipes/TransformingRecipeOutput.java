package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

/**
 * Allows for using vanilla recipe builders with custom recipe implementations based on vanilla recipe types.
 */
public interface TransformingRecipeOutput extends RecipeOutput {

    static RecipeOutput transformed(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) {
        return ProxyImpl.get().getTransformingRecipeOutput(recipeOutput, operator);
    }

    RecipeOutput recipeOutput();

    UnaryOperator<Recipe<?>> operator();

    @Override
    default void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
        this.recipeOutput().accept(id, this.operator().apply(recipe), advancement);
    }

    @Override
    default Advancement.Builder advancement() {
        return this.recipeOutput().advancement();
    }
}
