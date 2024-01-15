package fuzs.puzzleslib.fabric.impl.init;

import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public final class FabricPotionBrewingRegistry implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to item is null");
        FabricBrewingRecipeRegistry.registerItemRecipe(from, ingredient, to);
    }

    @Override
    public void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container item is null");
        PotionBrewing.addContainer(container);
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to potion is null");
        FabricBrewingRecipeRegistry.registerPotionRecipe(from, ingredient, to);
    }
}
