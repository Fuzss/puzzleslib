package fuzs.puzzleslib.fabric.impl.init;

import fuzs.puzzleslib.api.event.v1.server.RegisterPotionBrewingMixesCallback;
import net.minecraft.core.Holder;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public record FabricPotionBrewingBuilder(PotionBrewing.Builder builder) implements RegisterPotionBrewingMixesCallback.Builder {

    @Override
    public void registerPotionContainer(PotionItem item) {
        Objects.requireNonNull(item, "container item is null");
        this.builder.addContainer(item);
    }

    @Override
    public void registerContainerRecipe(PotionItem inputItem, Ingredient ingredient, PotionItem outputItem) {
        Objects.requireNonNull(inputItem, "input item is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(outputItem, "output item is null");
        this.builder.registerItemRecipe(inputItem, ingredient, outputItem);
    }

    @Override
    public void registerPotionRecipe(Holder<Potion> intputPotion, Ingredient ingredient, Holder<Potion> outputPotion) {
        Objects.requireNonNull(intputPotion, "input potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(outputPotion, "output potion is null");
        this.builder.registerPotionRecipe(intputPotion, ingredient, outputPotion);
    }
}
