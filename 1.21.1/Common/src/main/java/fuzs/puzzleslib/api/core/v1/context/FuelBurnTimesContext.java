package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.level.ItemLike;

/**
 * Applies fuel burn times instead of implementing this on the item side.
 */
@Deprecated
@FunctionalInterface
public interface FuelBurnTimesContext {

    /**
     * Registers an <code>item</code> as a fuel with the given <code>burnTime</code>.
     *
     * @param burnTime burn time in ticks
     * @param items    items to add <code>burnTime</code> to
     */
    void registerFuel(int burnTime, ItemLike... items);
}
