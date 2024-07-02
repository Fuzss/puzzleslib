package fuzs.puzzleslib.impl.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.function.BiConsumer;
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
        if (recipeSerializer == null) ContentRegistrationFlags.throwForFlag(ContentRegistrationFlags.COPY_TAG_RECIPES);
        return recipeSerializer;
    }

    static void registerSerializers(BiConsumer<String, Supplier<RecipeSerializer<?>>> registrar) {
        registrar.accept(SHAPED_RECIPE_SERIALIZER_ID, () -> new Serializer<>(new ShapedRecipe.Serializer(),
                CopyTagShapedRecipe::new
        ));
        registrar.accept(SHAPELESS_RECIPE_SERIALIZER_ID, () -> new Serializer<>(new ShapelessRecipe.Serializer(), CopyTagShapelessRecipe::new));
    }

    Ingredient getCopyTagSource();

    default void tryCopyTagToResult(ItemStack result, CraftingContainer craftingContainer) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (this.getCopyTagSource().test(itemStack) && itemStack.hasTag()) {
                result.setTag(itemStack.getComponents().copy());
                return;
            }
        }
    }

    @FunctionalInterface
    interface Factory<T extends CraftingRecipe, S extends CraftingRecipe & CopyTagRecipe> {

        S apply(RecipeSerializer<?> recipeSerializer, T craftingRecipe, Ingredient ingredient);
    }

    record Serializer<T extends CraftingRecipe, S extends CraftingRecipe & CopyTagRecipe>(
            RecipeSerializer<T> serializer, Factory<T, S> factory) implements RecipeSerializer<S> {

        @Override
        public Codec<S> codec() {
            return RecordCodecBuilder.create((instance) -> {
                return instance.group(((MapCodec.MapCodecCodec<T>) this.serializer.codec()).codec().forGetter((arg) -> {
                    return (T) arg;
                }), Ingredient.CODEC.fieldOf("copy_from").forGetter((arg) -> {
                    return arg.getCopyTagSource();
                })).apply(instance, (T craftingRecipe, Ingredient ingredient) -> this.factory.apply(this, craftingRecipe, ingredient));
            });
        }

        @Override
        public S fromNetwork(FriendlyByteBuf buffer) {
            T recipe = this.serializer.fromNetwork(buffer);
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            return this.factory.apply(this, recipe, ingredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, S recipe) {
            this.serializer.toNetwork(buffer, (T) recipe);
            recipe.getCopyTagSource().toNetwork(buffer);
        }
    }
}
