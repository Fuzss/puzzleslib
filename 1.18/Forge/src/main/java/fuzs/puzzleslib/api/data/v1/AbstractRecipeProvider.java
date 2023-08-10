package fuzs.puzzleslib.api.data.v1;

import com.google.common.base.Preconditions;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractRecipeProvider extends RecipeProvider {

    public AbstractRecipeProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator());
    }

    public AbstractRecipeProvider(DataGenerator packOutput) {
        super(packOutput);
    }

    protected abstract void buildRecipes(Consumer<FinishedRecipe> exporter);

    @Override
    protected final void buildCraftingRecipes(Consumer<FinishedRecipe> exporter) {
        this.buildRecipes(exporter);
    }

    protected static String getHasName(ItemLike... items) {
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        return "has_" + Stream.of(items).map(RecipeProvider::getItemName).collect(Collectors.joining("_and_"));
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike... items) {
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        return inventoryTrigger(ItemPredicate.Builder.item().of(items).build());
    }
}
