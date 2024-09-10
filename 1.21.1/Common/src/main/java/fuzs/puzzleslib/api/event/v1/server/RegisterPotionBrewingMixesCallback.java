package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

@FunctionalInterface
public interface RegisterPotionBrewingMixesCallback {
    EventInvoker<RegisterPotionBrewingMixesCallback> EVENT = EventInvoker.lookup(RegisterPotionBrewingMixesCallback.class);

    /**
     * Called when potion brewing recipes are set up in {@link net.minecraft.world.item.alchemy.PotionBrewing}.
     *
     * @param builder a builder instance wrapping {@link net.minecraft.world.item.alchemy.PotionBrewing.Builder}
     */
    void onRegisterPotionBrewingMixes(Builder builder);

    /**
     * A registry for adding new recipes valid for the brewing stand.
     * <p>
     * The implementation also supports {@link Ingredient} in addition to {@link Item}.
     */
    interface Builder {

        /**
         * Register an item that can hold potions.
         *
         * @param item the potion container item
         */
        void registerPotionContainer(PotionItem item);

        /**
         * Register a brewing stand recipe that converts a potion item container to another form, the potion inside will
         * stay the same. E.g. in vanilla convert a normal potion to a splash potion by adding gunpowder.
         *
         * @param from the base potion container item
         * @param item ingredient used for the conversion
         * @param to   the output potion container item
         */
        default void registerContainerRecipe(PotionItem from, Item item, PotionItem to) {
            this.registerContainerRecipe(from, Ingredient.of(item), to);
        }

        /**
         * Register a brewing stand recipe that converts a potion item container to another form, the potion inside will
         * stay the same. E.g. in vanilla convert a normal potion to a splash potion by adding gunpowder.
         *
         * @param from       the base potion container item
         * @param ingredient ingredient used for the conversion
         * @param to         the output potion container item
         */
        void registerContainerRecipe(PotionItem from, Ingredient ingredient, PotionItem to);

        /**
         * Register a brewing stand recipe that converts a potion to another potion, the potion item container will stay the
         * same. E.g. in vanilla convert a night vision potion to an invisibility potion by adding fermented spider eye.
         *
         * @param from the base potion
         * @param item ingredient used for the conversion
         * @param to   the output potion
         */
        default void registerPotionRecipe(Holder<Potion> from, Item item, Holder<Potion> to) {
            this.registerPotionRecipe(from, Ingredient.of(item), to);
        }

        /**
         * Register a brewing stand recipe that converts a potion to another potion, the potion item container will stay the
         * same. E.g. in vanilla convert a night vision potion to an invisibility potion by adding fermented spider eye.
         *
         * @param from       the base potion
         * @param ingredient ingredient used for the conversion
         * @param to         the output potion
         */
        void registerPotionRecipe(Holder<Potion> from, Ingredient ingredient, Holder<Potion> to);

        /**
         * Register a brewing stand recipe that converts an awkward potion to another potion. E.g. in vanilla convert an
         * awkward potion to a strength potion by adding blaze powder.
         * <p>
         * Additionally, registers a recipe for converting a water potion to a mundane potion using the provided ingredient,
         * as is possible for all vanilla ingredients.
         *
         * @param item ingredient used for the conversion
         * @param to   the output potion
         */
        default void registerStartPotionRecipe(Item item, Holder<Potion> to) {
            this.registerStartPotionRecipe(Ingredient.of(item), to);
        }

        /**
         * Register a brewing stand recipe that converts an awkward potion to another potion. E.g. in vanilla convert an
         * awkward potion to a strength potion by adding blaze powder.
         * <p>
         * Additionally, registers a recipe for converting a water potion to a mundane potion using the provided ingredient,
         * as is possible for all vanilla ingredients.
         *
         * @param ingredient ingredient used for the conversion
         * @param to         the output potion
         */
        default void registerStartPotionRecipe(Ingredient ingredient, Holder<Potion> to) {
            this.registerPotionRecipe(Potions.WATER, ingredient, Potions.MUNDANE);
            this.registerPotionRecipe(Potions.AWKWARD, ingredient, to);
        }
    }
}
