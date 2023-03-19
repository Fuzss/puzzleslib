package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.level.ItemLike;

/**
 * Applies fuel burn times instead of implementing this on the item side.
 */
@FunctionalInterface
public interface FuelBurnTimesContext {

    /**
     * Registers an <code>item</code> as a fuel with the given <code>burnTime</code>.
     *
     * @param items    items to add
     * @param burnTime burn time in ticks
     */
    void registerFuel(int burnTime, ItemLike item, ItemLike... items);
}
