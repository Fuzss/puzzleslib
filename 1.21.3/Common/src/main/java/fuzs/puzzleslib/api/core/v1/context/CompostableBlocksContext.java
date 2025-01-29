package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.level.ItemLike;

/**
 * Register items for usage with the composter block.
 */
@FunctionalInterface
public interface CompostableBlocksContext {

    /**
     * Register items for usage with the composter block.
     *
     * @param compostingChance the chance the compost level will increase, allowed values range from {@code 0.0} to
     *                         {@code 1.0} inclusive
     * @param items            the items to add with the provided chance
     */
    void registerCompostable(float compostingChance, ItemLike... items);
}
