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

    protected static String getHasName(ItemLike... items) {
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        return "has_" + Stream.of(items).map(RecipeProvider::getItemName).collect(Collectors.joining("_and_"));
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike... items) {
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        return inventoryTrigger(ItemPredicate.Builder.item().of(items).build());
    }
}
