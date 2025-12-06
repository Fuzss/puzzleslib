package fuzs.puzzleslib.api.item.v2.crafting;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

/**
 * A helper class for obtaining platform specific {@link Ingredient} implementations.
 * <p>Note that when using those ingredients in recipes not all ingredients might serialize to a vanilla-compatible
 * format, meaning they will not be readable on other mod loaders as well.
 */
public interface CombinedIngredients {
    CombinedIngredients INSTANCE = ProxyImpl.get().getCombinedIngredients();

    /**
     * Creates an ingredient that matches when its sub-ingredients all match.
     *
     * @param ingredients the sub-ingredients
     * @return the compound ingredient
     */
    Ingredient all(Ingredient... ingredients);

    /**
     * Creates an ingredient that matches when any of its sub-ingredients matches.
     *
     * @param ingredients the sub-ingredients
     * @return the compound ingredient
     */
    Ingredient any(Ingredient... ingredients);

    /**
     * Creates an ingredient that matches if its base ingredient matches, and its subtracted ingredient <strong>does
     * not</strong> match.
     *
     * @param ingredient the ingredient that must match
     * @param subtracted the ingredient that cannot match
     * @return the compound ingredient
     */
    Ingredient difference(Ingredient ingredient, Ingredient subtracted);

    /**
     * Creates an ingredient that matches the passed template item and data components.
     *
     * @param item       the item to match
     * @param components the data components to match
     * @return the compound ingredient
     */
    default Ingredient components(ItemLike item, DataComponentPatch components) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(components, "components is null");
        ItemStack itemStack = new ItemStack(item);
        itemStack.applyComponents(components);
        return this.components(itemStack);
    }

    /**
     * Creates an ingredient that matches the passed template item stack, including data components.
     * <p>
     * Note that the count of the stack is ignored.
     *
     * @param itemStack the item stack to construct an ingredient from
     * @return the compound ingredient
     */
    Ingredient components(ItemStack itemStack);
}
