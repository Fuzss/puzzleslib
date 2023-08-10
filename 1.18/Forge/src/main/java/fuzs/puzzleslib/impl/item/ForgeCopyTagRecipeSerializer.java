package fuzs.puzzleslib.impl.item;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.BiFunction;

public class ForgeCopyTagRecipeSerializer<T extends CraftingRecipe, S extends CraftingRecipe & CopyTagRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements CopyTagRecipe.Serializer<T, S> {
    private final RecipeSerializer<T> serializer;
    private final BiFunction<T, Ingredient, S> factory;

    public ForgeCopyTagRecipeSerializer(RecipeSerializer<T> serializer, BiFunction<T, Ingredient, S> factory) {
        this.serializer = serializer;
        this.factory = factory;
    }

    @Override
    public RecipeSerializer<T> serializer() {
        return this.serializer;
    }

    @Override
    public BiFunction<T, Ingredient, S> factory() {
        return this.factory;
    }
}
