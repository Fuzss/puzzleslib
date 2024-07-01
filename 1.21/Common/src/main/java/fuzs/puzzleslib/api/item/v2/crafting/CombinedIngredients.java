package fuzs.puzzleslib.api.item.v2.crafting;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A helper class for obtaining platform specific {@link Ingredient} implementations.
 * <p>Note that when using those ingredients in recipes not all ingredients might serialize to a vanilla-compatible format,
 * meaning they will not be readable on other mod loaders as well.
 */
public interface CombinedIngredients {
    CombinedIngredients INSTANCE = CommonFactories.INSTANCE.getCombinedIngredients();

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
     * Creates an ingredient that matches if its base ingredient matches, and its subtracted ingredient <strong>does not</strong> match.
     *
     * @param ingredient the ingredient that must match
     * @param subtracted the ingredient that cannot match
     * @return the compound ingredient
     */
    Ingredient difference(Ingredient ingredient, Ingredient subtracted);

    /**
     * Creates an ingredient that matches the passed template item and nbt.
     *
     * <p>In strict mode, passing a {@code null} {@code nbt} is allowed, and will only match stacks with {@code null} NBT.
     * In partial mode, passing a {@code null} {@code nbt} is <strong>not</strong> allowed, as it would always match.
     *
     * @param item   the item to match
     * @param nbt    the item stack tag to match
     * @param strict when <code>true</code> the exact NBT must match, when <code>false</code> the ingredient NBT must be a subset of the stack NBT
     * @return the compound ingredient
     */
    default Ingredient nbt(ItemLike item, @Nullable CompoundTag nbt, boolean strict) {
        Objects.requireNonNull(item, "item is null");
        if (!strict) Objects.requireNonNull(nbt, "nbt is null");
        ItemStack stack = new ItemStack(item);
        stack.setTag(nbt);
        return this.nbt(stack, strict);
    }

    /**
     * Creates an ingredient that matches the passed template stack, including NBT. Note that the count of the stack is ignored.
     *
     * <p>In strict mode, passing an {@link ItemStack} where {@link ItemStack#getTag()} is {@code null} is allowed, and will only match stacks with {@code null} NBT.
     * In partial mode, passing an {@link ItemStack} where {@link ItemStack#getTag()} is {@code null} is <strong>not</strong> allowed, as it would always match.
     *
     * @param stack  the item stack to construct an ingredient from
     * @param strict when <code>true</code> the exact NBT must match, when <code>false</code> the ingredient NBT must be a subset of the stack NBT
     * @return the compound ingredient
     */
    Ingredient nbt(ItemStack stack, boolean strict);
}
