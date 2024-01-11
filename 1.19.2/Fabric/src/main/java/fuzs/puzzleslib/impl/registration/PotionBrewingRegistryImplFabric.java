package fuzs.puzzleslib.impl.registration;

import fuzs.puzzleslib.init.PotionBrewingRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public class PotionBrewingRegistryImplFabric implements PotionBrewingRegistry {

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
