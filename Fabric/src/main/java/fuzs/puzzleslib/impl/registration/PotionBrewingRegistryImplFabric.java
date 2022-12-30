package fuzs.puzzleslib.impl.registration;

import fuzs.puzzleslib.mixin.accessor.PotionBrewingAccessor;
import fuzs.puzzleslib.init.PotionBrewingRegistry;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public class PotionBrewingRegistryImplFabric implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(to, "to item is null");
        PotionBrewingAccessor.puzzleslib$getContainerMixes().add(new PotionBrewing.Mix<>(from, ingredient, to));
    }

    @Override
    public void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container is null");
        PotionBrewing.addContainer(container);
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(to, "to potion is null");
        PotionBrewingAccessor.puzzleslib$getPotionMixes().add(new PotionBrewing.Mix<>(from, ingredient, to));
    }
}
