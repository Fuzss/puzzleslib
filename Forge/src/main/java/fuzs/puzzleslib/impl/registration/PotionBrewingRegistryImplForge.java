package fuzs.puzzleslib.impl.registration;

import fuzs.puzzleslib.mixin.accessor.PotionBrewingAccessor;
import fuzs.puzzleslib.mixin.accessor.PotionBrewingForgeAccessor;
import fuzs.puzzleslib.init.PotionBrewingRegistry;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class PotionBrewingRegistryImplForge implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(to, "to item is null");
        PotionBrewingAccessor.puzzleslib$getContainerMixes().add(new PotionBrewing.Mix<>(ForgeRegistries.ITEMS, from, ingredient, to));
    }

    @Override
    public void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container is null");
        PotionBrewingForgeAccessor.puzzleslib$getAllowedContainers().add(Ingredient.of(container));
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(to, "to potion is null");
        PotionBrewingAccessor.puzzleslib$getPotionMixes().add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, from, ingredient, to));
    }
}
