package fuzs.puzzleslib.neoforge.api.data.v1;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.neoforge.api.data.v1.recipes.ForwardingFinishedRecipe;
import fuzs.puzzleslib.api.item.v2.LegacySmithingTransformRecipe;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractRecipeProvider extends RecipeProvider {

    public AbstractRecipeProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator().getPackOutput());
    }

    public AbstractRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected abstract void buildRecipes(Consumer<FinishedRecipe> exporter);

    /**
     * Registers a simplified smithing recipe that allows for upgrading gear without the need for a smithing template, just like the old smithing used to work.
     *
     * @param modId          the mod id used for retrieving the {@link net.minecraft.world.item.crafting.RecipeSerializer},
     *                       it is important to enable {@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#LEGACY_SMITHING} for the serializer to be registered
     * @param exporter       the recipe saver
     * @param base           the base item for the smithing upgrade, like diamond armor and tools in vanilla
     * @param recipeCategory the {@link RecipeCategory} for the recipe book
     * @param result         the result item from the smithing upgrade, like netherite armor and tools in vanilla
     * @deprecated replaced by {@link fuzs.puzzleslib.impl.item.CopyTagShapelessRecipe}
     */
    @Deprecated(forRemoval = true)
    protected static void legacyNetheriteSmithing(String modId, Consumer<FinishedRecipe> exporter, Item base, RecipeCategory recipeCategory, Item result) {
        legacyNetheriteSmithing(modId, exporter, base, Items.NETHERITE_INGOT, recipeCategory, result);
    }

    /**
     * Registers a simplified smithing recipe that allows for upgrading gear without the need for a smithing template, just like the old smithing used to work.
     *
     * @param modId          the mod id used for retrieving the {@link net.minecraft.world.item.crafting.RecipeSerializer},
     *                       it is important to enable {@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#LEGACY_SMITHING} for the serializer to be registered
     * @param exporter       the recipe saver
     * @param base           the base item for the smithing upgrade, like diamond armor and tools in vanilla
     * @param addition       the upgrade item, usually a netherite ingot in vanilla
     * @param recipeCategory the {@link RecipeCategory} for the recipe book
     * @param result         the result item from the smithing upgrade, like netherite armor and tools in vanilla
     * @deprecated replaced by {@link fuzs.puzzleslib.impl.item.CopyTagShapelessRecipe}
     */
    @Deprecated(forRemoval = true)
    protected static void legacyNetheriteSmithing(String modId, Consumer<FinishedRecipe> exporter, Item base, Item addition, RecipeCategory recipeCategory, Item result) {
        new SmithingTransformRecipeBuilder(LegacySmithingTransformRecipe.getModSerializer(modId), Ingredient.of(), Ingredient.of(base), Ingredient.of(addition), recipeCategory, result)
                .unlocks(getHasName(addition), has(addition))
                .save(finishedRecipe -> {
                    exporter.accept(new ForwardingFinishedRecipe(finishedRecipe, json -> {
                        json.remove("template");
                    }));
        }, new ResourceLocation(modId, getItemName(result) + "_crafting_transform"));
    }

    protected static String getHasName(ItemLike... items) {
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        return "has_" + Stream.of(items).map(RecipeProvider::getItemName).collect(Collectors.joining("_and_"));
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike... items) {
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        return inventoryTrigger(ItemPredicate.Builder.item().of(items).build());
    }

    protected static void stonecutterResultFromBase(String modId, Consumer<FinishedRecipe> exporter, RecipeCategory category, ItemLike result, ItemLike ingredient) {
        stonecutterResultFromBase(modId, exporter, category, result, ingredient, 1);
    }

    protected static void stonecutterResultFromBase(String modId, Consumer<FinishedRecipe> exporter, RecipeCategory category, ItemLike result, ItemLike ingredient, int resultCount) {
        String recipeId = getConversionRecipeName(result, ingredient) + "_stonecutting";
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), category, result, resultCount).unlockedBy(getHasName(ingredient), has(ingredient)).save(exporter, new ResourceLocation(modId, recipeId));
    }
}
