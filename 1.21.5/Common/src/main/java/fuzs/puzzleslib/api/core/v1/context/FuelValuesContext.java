package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * Register items as furnace fuel.
 */
@Deprecated(forRemoval = true)
public interface FuelValuesContext {

    /**
     * @param item      the fuel item
     * @param fuelValue the burn time in ticks, should be based on {@link #fuelBaseValue()}
     */
    void registerFuel(Holder<? extends ItemLike> item, int fuelValue);

    /**
     * @param tagKey    the fuel items
     * @param fuelValue the burn time in ticks, should be based on {@link #fuelBaseValue()}
     */
    void registerFuel(TagKey<Item> tagKey, int fuelValue);

    /**
     * @return the fuel base value unit used as a multiplier
     */
    default int fuelBaseValue() {
        return 200;
    }
}
