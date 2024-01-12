package fuzs.puzzleslib.forge.impl.init;

import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.forge.mixin.accessor.PotionBrewingForgeAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * We do not use Forge's {@link net.minecraftforge.common.brewing.BrewingRecipeRegistry},
 * as recipes added there are not fully supported by recipe viewer mods such as JEI.
 */
public final class PotionBrewingRegistryForge implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to item is null");
        PotionBrewing.Mix<Item> mix = new PotionBrewing.Mix<>(ForgeRegistries.ITEMS, from, ingredient, to);
        PotionBrewingForgeAccessor.puzzleslib$getContainerMixes().add(mix);
    }

    @Override
    public void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container item is null");
        Ingredient ingredient = Ingredient.of(container);
        PotionBrewingForgeAccessor.puzzleslib$getAllowedContainers().add(ingredient);
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to potion is null");
        PotionBrewing.Mix<Potion> mix = new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, from, ingredient, to);
        PotionBrewingForgeAccessor.puzzleslib$getPotionMixes().add(mix);
    }
}
