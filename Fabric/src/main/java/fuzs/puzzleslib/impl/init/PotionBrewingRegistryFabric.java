package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public final class PotionBrewingRegistryFabric implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        FabricBrewingRecipeRegistry.registerItemRecipe(from, ingredient, to);
    }

    @Override
    public void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container is null");
        PotionBrewing.addContainer(container);
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        FabricBrewingRecipeRegistry.registerPotionRecipe(from, ingredient, to);
    }
}
