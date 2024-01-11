package fuzs.puzzleslib.api.init.v2;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * A registry for adding new recipes valid for the brewing stand.
 * Vanilla methods are exposed on Fabric, require {@link Item} instead of {@link Ingredient} though, while the implementation supports {@link Ingredient}.
 * Forge uses a whole different system based on {@link Ingredient}, which does not allow specifying {@link Potion}s as vanilla does.
 */
public interface PotionBrewingRegistry {
    PotionBrewingRegistry INSTANCE = CommonFactories.INSTANCE.getPotionBrewingRegistry();

    /**
     * Register an item that can hold potions.
     * <p>We don't take an {@link Ingredient} here as Fabric natively doesn't support that and there isn't really much point to it.
     *
     * @param container the potion container item
     */
    void registerPotionContainer(PotionItem container);

    /**
     * Register a brewing stand recipe that converts a potion item container to another form, the potion inside will stay the same.
     * E.g. in vanilla convert a normal potion to a splash potion by adding gunpowder.
     *
     * @param from the base potion container item
     * @param ingredient ingredient used for the conversion
     * @param to         the output potion container item
     */
    default void registerContainerRecipe(PotionItem from, Item ingredient, PotionItem to) {
        this.registerContainerRecipe(from, Ingredient.of(ingredient), to);
    }

    /**
     * Register a brewing stand recipe that converts a potion item container to another form, the potion inside will stay the same.
     * E.g. in vanilla convert a normal potion to a splash potion by adding gunpowder.
     *
     * @param from the base potion container item
     * @param ingredient ingredient used for the conversion
     * @param to         the output potion container item
     */
    void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to);

    /**
     * Register a brewing stand recipe that converts a potion to another potion, the potion item container will stay the same.
     * E.g. in vanilla convert a night vision potion to an invisibility potion by adding fermented spider eye.
     *
     * @param from the base potion container item
     * @param ingredient ingredient used for the conversion
     * @param to         the output potion container item
     */
    default void registerPotionRecipe(Potion from, Item ingredient, Potion to) {
        this.registerPotionRecipe(from, Ingredient.of(ingredient), to);
    }

    /**
     * Register a brewing stand recipe that converts a potion to another potion, the potion item container will stay the same.
     * E.g. in vanilla convert a night vision potion to an invisibility potion by adding fermented spider eye.
     *
     * @param from the base potion container item
     * @param ingredient ingredient used for the conversion
     * @param to         the output potion container item
     */
    void registerPotionRecipe(Potion from, Ingredient ingredient, Potion to);
}
