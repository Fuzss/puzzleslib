package fuzs.puzzleslib.impl.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.init.v3.registry.ContentRegistrationHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface CustomTransmuteRecipe {
    /**
     * The shaped recipe serializer id that is used during registration.
     */
    String TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID = "crafting_transmute_shaped";
    /**
     * The shapeless recipe serializer id that is used during registration.
     */
    String TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID = "crafting_transmute_shapeless";

    /**
     * Finds the mod-specific {@link RecipeSerializer} in the registry.
     * <p>
     * The serializer must manually be registered via
     * {@link ContentRegistrationHelper#registerTransmuteRecipeSerializers(RegistryManager)}.
     *
     * @param modId              the mod id to find the serializer for
     * @param recipeSerializerId the serializer string id, either {@link #TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID} or
     *                           {@link #TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID}
     * @return the serializer
     */
    static RecipeSerializer<?> getModSerializer(String modId, String recipeSerializerId) {
        RecipeSerializer<?> recipeSerializer = BuiltInRegistries.RECIPE_SERIALIZER.getValue(Identifier.fromNamespaceAndPath(
                modId,
                recipeSerializerId));
        Objects.requireNonNull(recipeSerializer,
                "recipe serializer '" + Identifier.fromNamespaceAndPath(modId, recipeSerializerId)
                        + "' not registered");
        return recipeSerializer;
    }

    static void registerSerializers(BiConsumer<String, Supplier<RecipeSerializer<?>>> registrar) {
        registrar.accept(TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID,
                () -> new Serializer<>(new ShapedRecipe.Serializer(), TransmuteShapedRecipe::new));
        registrar.accept(TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID,
                () -> new Serializer<>(new ShapelessRecipe.Serializer(), TransmuteShapelessRecipe::new));
    }

    Ingredient getInput();

    default void transmuteInput(ItemStack result, CraftingInput craftingInput) {
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack itemStack = craftingInput.getItem(i);
            if (this.getInput().test(itemStack)) {
                result.applyComponents(itemStack.getComponentsPatch());
                return;
            }
        }
    }

    @FunctionalInterface
    interface Factory<T extends CraftingRecipe, S extends CraftingRecipe & CustomTransmuteRecipe> {

        S apply(RecipeSerializer<?> recipeSerializer, T craftingRecipe, Ingredient ingredient);
    }

    record Serializer<R1 extends CraftingRecipe, R2 extends CraftingRecipe & CustomTransmuteRecipe>(RecipeSerializer<R1> serializer,
                                                                                                    Factory<R1, R2> factory) implements RecipeSerializer<R2> {

        @Override
        public MapCodec<R2> codec() {
            return RecordCodecBuilder.mapCodec((instance) -> {
                return instance.group(this.serializer.codec().forGetter((R2 arg) -> {
                            return (R1) arg;
                        }), Ingredient.CODEC.fieldOf("input").forGetter(CustomTransmuteRecipe::getInput))
                        .apply(instance,
                                (R1 craftingRecipe, Ingredient ingredient) -> this.factory.apply(this,
                                        craftingRecipe,
                                        ingredient));
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
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getInput());
        }
    }
}
