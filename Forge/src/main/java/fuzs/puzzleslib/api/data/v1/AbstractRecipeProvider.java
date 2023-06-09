package fuzs.puzzleslib.api.data.v1;

import fuzs.puzzleslib.api.item.v2.LegacySmithingTransformRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public abstract class AbstractRecipeProvider extends RecipeProvider {

    public AbstractRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected abstract void buildRecipes(Consumer<FinishedRecipe> exporter);

    /**
     * Registers a simplified smithing recipe that allows for upgrading gear without the need for a smithing template, just like the old smithing used to work.
     *
     * @param modId    the mod id used for retrieving the {@link net.minecraft.world.item.crafting.RecipeSerializer},
     *                 it is important to enable {@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#LEGACY_SMITHING} for the serializer to be registered
     * @param exporter the recipe saver
     * @param base     the base item for the smithing upgrade, like diamond armor and tools in vanilla
     * @param category the {@link RecipeCategory} for the recipe book
     * @param result   the result item from the smithing upgrade, like netherite armor and tools in vanilla
     */
    protected static void legacyNetheriteSmithing(String modId, Consumer<FinishedRecipe> exporter, Item base, RecipeCategory category, Item result) {
        legacyNetheriteSmithing(modId, exporter, base, Items.NETHERITE_INGOT, category, result);
    }

    /**
     * Registers a simplified smithing recipe that allows for upgrading gear without the need for a smithing template, just like the old smithing used to work.
     *
     * @param modId    the mod id used for retrieving the {@link net.minecraft.world.item.crafting.RecipeSerializer},
     *                 it is important to enable {@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#LEGACY_SMITHING} for the serializer to be registered
     * @param exporter the recipe saver
     * @param base     the base item for the smithing upgrade, like diamond armor and tools in vanilla
     * @param addition the upgrade item, usually a netherite ingot in vanilla
     * @param category the {@link RecipeCategory} for the recipe book
     * @param result   the result item from the smithing upgrade, like netherite armor and tools in vanilla
     */
    protected static void legacyNetheriteSmithing(String modId, Consumer<FinishedRecipe> exporter, Item base, Item addition, RecipeCategory category, Item result) {
        new SmithingTransformRecipeBuilder(LegacySmithingTransformRecipe.getModSerializer(modId), Ingredient.of(), Ingredient.of(base), Ingredient.of(addition), category, result)
                .unlocks(getHasName(addition), has(addition))
                .save(exporter, new ResourceLocation(modId, getItemName(result) + "_smithing_transform"));
    }
}
