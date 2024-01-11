package fuzs.puzzleslib.forge.api.data.v1.recipes;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ForwardingFinishedRecipe implements FinishedRecipe {
    protected final FinishedRecipe finishedRecipe;
    protected final Consumer<JsonObject> serializeRecipeData;
    @Nullable
    protected final RecipeSerializer<?> recipeSerializer;

    public ForwardingFinishedRecipe(FinishedRecipe finishedRecipe) {
        this(finishedRecipe, t -> {});
    }

    public ForwardingFinishedRecipe(FinishedRecipe finishedRecipe, Consumer<JsonObject> serializeRecipeData) {
        this(finishedRecipe, serializeRecipeData, null);
    }

    public ForwardingFinishedRecipe(FinishedRecipe finishedRecipe, Consumer<JsonObject> serializeRecipeData, @Nullable RecipeSerializer<?> recipeSerializer) {
        this.finishedRecipe = finishedRecipe;
        this.serializeRecipeData = serializeRecipeData;
        this.recipeSerializer = recipeSerializer;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        this.finishedRecipe.serializeRecipeData(json);
        this.serializeRecipeData.accept(json);
    }

    @Override
    public ResourceLocation getId() {
        return this.finishedRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getType() {
        return this.recipeSerializer != null ? this.recipeSerializer : this.finishedRecipe.getType();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return this.finishedRecipe.serializeAdvancement();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return this.finishedRecipe.getAdvancementId();
    }
}
