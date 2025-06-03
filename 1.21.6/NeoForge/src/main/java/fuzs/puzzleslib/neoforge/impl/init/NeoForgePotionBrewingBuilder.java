package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.event.v1.server.RegisterPotionBrewingMixesCallback;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

/**
 * We do not use Forge's {@link net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry}, as recipes added there are
 * not fully supported by recipe viewer mods such as JEI.
 */
public record NeoForgePotionBrewingBuilder(PotionBrewing.Builder builder) implements RegisterPotionBrewingMixesCallback.Builder {

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
        if (from.isEnabled(this.builder.enabledFeatures) && to.isEnabled(this.builder.enabledFeatures)) {
            PotionBrewing.Mix<Item> mix = new PotionBrewing.Mix<>(from.builtInRegistryHolder(),
                    ingredient,
                    to.builtInRegistryHolder()
            );
            this.builder.containerMixes.add(mix);
        }
    }

    @Override
    public void registerPotionRecipe(Holder<Potion> from, Ingredient ingredient, Holder<Potion> to) {
        Objects.requireNonNull(from, "from potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(to, "to potion is null");
        if (from.value().isEnabled(this.builder.enabledFeatures) &&
                to.value().isEnabled(this.builder.enabledFeatures)) {
            PotionBrewing.Mix<Potion> mix = new PotionBrewing.Mix<>(from, ingredient, to);
            this.builder.potionMixes.add(mix);
        }
    }
}
