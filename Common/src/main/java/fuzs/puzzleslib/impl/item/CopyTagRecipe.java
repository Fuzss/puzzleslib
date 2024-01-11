package fuzs.puzzleslib.impl.item;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface CopyTagRecipe {
    /**
     * The shaped recipe serializer id that is used during registration.
     */
    String SHAPED_RECIPE_SERIALIZER_ID = "copy_tag_shaped_recipe";
    /**
     * The shapeless recipe serializer id that is used during registration.
     */
    String SHAPELESS_RECIPE_SERIALIZER_ID = "copy_tag_shapeless_recipe";

    /**
     * Finds the mod-specific {@link RecipeSerializer} in the registry.
     * <p>{@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#COPY_TAG_RECIPES} must be enabled so the serializer is registered.
     *
     * @param modId              the mod id to find the serializer for
     * @param recipeSerializerId the serializer string id, either {@link #SHAPED_RECIPE_SERIALIZER_ID} or {@link #SHAPELESS_RECIPE_SERIALIZER_ID}
     * @return the serializer
     */
    static RecipeSerializer<?> getModSerializer(String modId, String recipeSerializerId) {
        RecipeSerializer<?> recipeSerializer = BuiltInRegistries.RECIPE_SERIALIZER.get(new ResourceLocation(modId, recipeSerializerId));
        Objects.requireNonNull(recipeSerializer, "%s serializer for %s is null".formatted(recipeSerializerId, modId));
        return recipeSerializer;
    }

    static void registerSerializers(BiConsumer<String, Supplier<RecipeSerializer<?>>> registrar) {
        registrar.accept(SHAPED_RECIPE_SERIALIZER_ID, () -> new Serializer<>(new ShapedRecipe.Serializer(), CopyTagShapedRecipe::new));
        registrar.accept(SHAPELESS_RECIPE_SERIALIZER_ID, () -> new Serializer<>(new ShapelessRecipe.Serializer(), CopyTagShapelessRecipe::new));
    }

    Ingredient getCopyTagSource();

    default void tryCopyTagToResult(ItemStack result, CraftingContainer craftingContainer) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (this.getCopyTagSource().test(itemStack) && itemStack.hasTag()) {
                result.setTag(itemStack.getTag().copy());
                return;
            }
        }
    }

    record Serializer<T extends CraftingRecipe, S extends CraftingRecipe & CopyTagRecipe>(
            RecipeSerializer<T> serializer, BiFunction<T, Ingredient, S> factory) implements RecipeSerializer<S> {

        @Override
        public S fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            T recipe = this.serializer.fromJson(recipeId, serializedRecipe);
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(serializedRecipe, "copy_from"));
            return this.factory.apply(recipe, ingredient);
        }

        @Override
        public S fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            T recipe = this.serializer.fromNetwork(recipeId, buffer);
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            return this.factory.apply(recipe, ingredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, S recipe) {
            this.serializer.toNetwork(buffer, (T) recipe);
            recipe.getCopyTagSource().toNetwork(buffer);
        }
    }
}
