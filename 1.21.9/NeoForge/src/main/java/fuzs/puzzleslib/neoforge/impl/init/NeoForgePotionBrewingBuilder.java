package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.event.v1.server.RegisterPotionBrewingMixesCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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
    public void registerContainerRecipe(PotionItem inputItem, Ingredient ingredient, PotionItem outputItem) {
        Objects.requireNonNull(inputItem, "input item is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(outputItem, "output item is null");
        if (inputItem.isEnabled(this.builder.enabledFeatures) && outputItem.isEnabled(this.builder.enabledFeatures)) {
            PotionBrewing.Mix<Item> mix = new PotionBrewing.Mix<>(BuiltInRegistries.ITEM.wrapAsHolder(inputItem),
                    ingredient,
                    BuiltInRegistries.ITEM.wrapAsHolder(outputItem));
            this.builder.containerMixes.add(mix);
        }
    }

    @Override
    public void registerPotionRecipe(Holder<Potion> intputPotion, Ingredient ingredient, Holder<Potion> outputPotion) {
        Objects.requireNonNull(intputPotion, "input potion is null");
        Objects.requireNonNull(ingredient, "ingredient is null");
        Objects.requireNonNull(outputPotion, "output potion is null");
        if (intputPotion.value().isEnabled(this.builder.enabledFeatures) && outputPotion.value()
                .isEnabled(this.builder.enabledFeatures)) {
            PotionBrewing.Mix<Potion> mix = new PotionBrewing.Mix<>(intputPotion, ingredient, outputPotion);
            this.builder.potionMixes.add(mix);
        }
    }
}
