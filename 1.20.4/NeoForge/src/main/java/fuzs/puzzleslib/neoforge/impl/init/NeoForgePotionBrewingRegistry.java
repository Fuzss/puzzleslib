package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v3.PotionBrewingRegistry;
import fuzs.puzzleslib.neoforge.mixin.accessor.PotionBrewingNeoForgeAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

/**
 * We do not use Forge's {@link net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry},
 * as recipes added there are not fully supported by recipe viewer mods such as JEI.
 */
public final class NeoForgePotionBrewingRegistry implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to item is null");
        PotionBrewing.Mix<Item> mix = new PotionBrewing.Mix<>(from, ingredient, to);
        PotionBrewingNeoForgeAccessor.puzzleslib$getContainerMixes().add(mix);
    }

    @Override
    public void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container item is null");
        Ingredient ingredient = Ingredient.of(container);
        PotionBrewingNeoForgeAccessor.puzzleslib$getAllowedContainers().add(ingredient);
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to potion is null");
        PotionBrewing.Mix<Potion> mix = new PotionBrewing.Mix<>(from, ingredient, to);
        PotionBrewingNeoForgeAccessor.puzzleslib$getPotionMixes().add(mix);
    }
}
