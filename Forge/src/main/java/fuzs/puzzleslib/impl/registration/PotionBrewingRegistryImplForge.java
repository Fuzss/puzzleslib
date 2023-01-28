package fuzs.puzzleslib.impl.registration;

import fuzs.puzzleslib.init.PotionBrewingRegistry;
import fuzs.puzzleslib.mixin.accessor.PotionBrewingForgeAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.Optional;

public class PotionBrewingRegistryImplForge implements PotionBrewingRegistry {

    @Override
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(to, "to item is null");
        BrewingRecipeRegistry.addRecipe(new MixBrewingRecipe<>(ForgeRegistries.ITEMS, from, ingredient, to) {

            @Override
            Optional<ItemStack> mix(PotionBrewing.Mix<Item> mix, ItemStack ingredient, Potion potion, Item item) {
                if (mix.from.get() == item && mix.ingredient.test(ingredient)) {
                    return Optional.of(PotionUtils.setPotion(new ItemStack(mix.to.get()), potion));
                }
                return Optional.empty();
            }
        });
    }

    @Override
    public synchronized void registerPotionContainer(PotionItem container) {
        Objects.requireNonNull(container, "container is null");
        PotionBrewingForgeAccessor.puzzleslib$getAllowedContainers().add(Ingredient.of(container));
    }

    @Override
    public void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to) {
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(to, "to potion is null");
        BrewingRecipeRegistry.addRecipe(new MixBrewingRecipe<>(ForgeRegistries.POTIONS, from, ingredient, to) {

            @Override
            Optional<ItemStack> mix(PotionBrewing.Mix<Potion> mix, ItemStack ingredient, Potion potion, Item item) {
                if (mix.from.get() == potion && mix.ingredient.test(ingredient)) {
                    return Optional.of(PotionUtils.setPotion(new ItemStack(item), mix.to.get()));
                }
                return Optional.empty();
            }
        });
    }

    private static abstract class MixBrewingRecipe<T> extends VanillaBrewingRecipe {
        private final PotionBrewing.Mix<T> mix;

        public MixBrewingRecipe(IForgeRegistry<T> registry, T from, Ingredient ingredient, T to) {
            this.mix = new PotionBrewing.Mix<>(registry, from, ingredient, to);
        }

        @Override
        public boolean isIngredient(ItemStack stack) {
            return this.mix.ingredient.test(stack);
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (!input.isEmpty()) {
                Potion potion = PotionUtils.getPotion(input);
                Item item = input.getItem();
                return this.mix(this.mix, ingredient, potion, item).orElse(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        abstract Optional<ItemStack> mix(PotionBrewing.Mix<T> mix, ItemStack ingredient, Potion potion, Item item);
    }
}
