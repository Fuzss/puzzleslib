package fuzs.puzzleslib.impl.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Deprecated
public interface CopyComponentsRecipe {
    /**
     * The shaped recipe serializer id that is used during registration.
     */
    String SHAPED_RECIPE_SERIALIZER_ID = "copy_components_shaped_recipe";
    /**
     * The shapeless recipe serializer id that is used during registration.
     */
    String SHAPELESS_RECIPE_SERIALIZER_ID = "copy_components_shapeless_recipe";

    /**
     * Finds the mod-specific {@link RecipeSerializer} in the registry.
     * <p>{@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#COPY_RECIPES} must be enabled so the
     * serializer is registered.
     *
     * @param modId              the mod id to find the serializer for
     * @param recipeSerializerId the serializer string id, either {@link #SHAPED_RECIPE_SERIALIZER_ID} or
     *                           {@link #SHAPELESS_RECIPE_SERIALIZER_ID}
     * @return the serializer
     */
    static RecipeSerializer<?> getModSerializer(String modId, String recipeSerializerId) {
        RecipeSerializer<?> recipeSerializer = BuiltInRegistries.RECIPE_SERIALIZER.get(ResourceLocationHelper.fromNamespaceAndPath(
                modId,
                recipeSerializerId
        ));
        if (recipeSerializer == null) ContentRegistrationFlags.throwForFlag(ContentRegistrationFlags.COPY_RECIPES);
        return recipeSerializer;
    }

    static void registerSerializers(BiConsumer<String, Supplier<RecipeSerializer<?>>> registrar) {
        registrar.accept(SHAPED_RECIPE_SERIALIZER_ID,
                () -> new Serializer<>(new ShapedRecipe.Serializer(), CopyComponentsShapedRecipe::new)
        );
        registrar.accept(SHAPELESS_RECIPE_SERIALIZER_ID,
                () -> new Serializer<>(new ShapelessRecipe.Serializer(), CopyComponentsShapelessRecipe::new)
        );
    }

    Ingredient getComponentsSource();

    default void copyComponentsToResult(ItemStack result, CraftingInput craftingInput) {
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack itemStack = craftingInput.getItem(i);
            if (this.getComponentsSource().test(itemStack)) {
                result.applyComponents(itemStack.getComponents());
                return;
            }
        }
    }

    @FunctionalInterface
    interface Factory<T extends CraftingRecipe, S extends CraftingRecipe & CopyComponentsRecipe> {

        S apply(RecipeSerializer<?> recipeSerializer, T craftingRecipe, Ingredient ingredient);
    }

    record Serializer<R1 extends CraftingRecipe, R2 extends CraftingRecipe & CopyComponentsRecipe>(RecipeSerializer<R1> serializer,
                                                                                                   Factory<R1, R2> factory) implements RecipeSerializer<R2> {

        @Override
        public MapCodec<R2> codec() {
            return RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(this.serializer.codec().forGetter((R2 arg) -> {
                            return (R1) arg;
                        }), Ingredient.CODEC.fieldOf("copy_from").forGetter(CopyComponentsRecipe::getComponentsSource))
                        .apply(instance,
                                (R1 craftingRecipe, Ingredient ingredient) -> this.factory.apply(this,
                                        craftingRecipe,
                                        ingredient
                                )
                        );
            });
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R2> streamCodec() {
            return StreamCodec.of(this::toNetwork, this::fromNetwork);
        }

        private R2 fromNetwork(RegistryFriendlyByteBuf buffer) {
            R1 recipe = this.serializer.streamCodec().decode(buffer);
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return this.factory.apply(this, recipe, ingredient);
        }

        private void toNetwork(RegistryFriendlyByteBuf buffer, R2 recipe) {
            this.serializer.streamCodec().encode(buffer, (R1) recipe);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getComponentsSource());
        }
    }
}
