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
    public void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to) {
        Objects.requireNonNull(from, "from item is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to item is null");
        this.builder.registerItemRecipe(from, ingredient, to);
    }

    @Override
    public void registerPotionRecipe(Holder<Potion> from, Ingredient ingredient, Holder<Potion> to) {
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to potion is null");
        this.builder.registerPotionRecipe(from, ingredient, to);
    }
}
