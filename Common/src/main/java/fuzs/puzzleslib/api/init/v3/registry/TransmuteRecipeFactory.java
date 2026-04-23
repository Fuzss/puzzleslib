package fuzs.puzzleslib.api.init.v3.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

@FunctionalInterface
public interface TransmuteRecipeFactory<T extends CraftingRecipe, S extends CraftingRecipe & CustomTransmuteRecipe> {
    S apply(ResourceKey<RecipeSerializer<?>> recipeSerializer, T craftingRecipe, Ingredient ingredient);

    static <R1 extends CraftingRecipe, R2 extends CraftingRecipe & CustomTransmuteRecipe> void register(RegistryManager registryManager, String serializerId, RecipeSerializer<R1> serializer, TransmuteRecipeFactory<R1, R2> factory) {
        ResourceKey<RecipeSerializer<?>> resourceKey = registryManager.makeResourceKey(Registries.RECIPE_SERIALIZER,
                serializerId);
        registryManager.register(Registries.RECIPE_SERIALIZER, resourceKey.identifier().getPath(), () -> {
            return serializer(resourceKey, serializer, factory);
        });
    }

    private static <R1 extends CraftingRecipe, R2 extends CraftingRecipe & CustomTransmuteRecipe> RecipeSerializer<R2> serializer(ResourceKey<RecipeSerializer<?>> resourceKey, RecipeSerializer<R1> recipeSerializer, TransmuteRecipeFactory<R1, R2> factory) {
        return new RecipeSerializer<>(codec(resourceKey, recipeSerializer.codec(), factory),
                streamCodec(resourceKey, recipeSerializer.streamCodec(), factory));
    }

    private static <R1 extends CraftingRecipe, R2 extends CraftingRecipe & CustomTransmuteRecipe> MapCodec<R2> codec(ResourceKey<RecipeSerializer<?>> resourceKey, MapCodec<R1> codec, TransmuteRecipeFactory<R1, R2> factory) {
        return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(codec.forGetter((R2 craftingRecipe) -> {
                        return (R1) craftingRecipe;
                    }), Ingredient.CODEC.fieldOf("input").forGetter(CustomTransmuteRecipe::getInput))
                    .apply(instance,
                            (R1 craftingRecipe, Ingredient ingredient) -> factory.apply(resourceKey,
                                    craftingRecipe,
                                    ingredient));
        });
    }

    private static <R1 extends CraftingRecipe, R2 extends CraftingRecipe & CustomTransmuteRecipe> StreamCodec<RegistryFriendlyByteBuf, R2> streamCodec(ResourceKey<RecipeSerializer<?>> resourceKey, StreamCodec<RegistryFriendlyByteBuf, R1> streamCodec, TransmuteRecipeFactory<R1, R2> factory) {
        return new StreamCodec<>() {
            @Override
            public R2 decode(RegistryFriendlyByteBuf input) {
                R1 recipe = streamCodec.decode(input);
                Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(input);
                return factory.apply(resourceKey, recipe, ingredient);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf output, R2 recipe) {
                streamCodec.encode(output, (R1) recipe);
                Ingredient.CONTENTS_STREAM_CODEC.encode(output, recipe.getInput());
            }
        };
    }
}
