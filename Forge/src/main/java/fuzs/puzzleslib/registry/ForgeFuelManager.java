package fuzs.puzzleslib.registry;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;

/**
 * define fuel burn times on Forge
 */
public class ForgeFuelManager implements FuelManager {
    /**
     * stored burn times
     */
    private final Object2IntOpenHashMap<Item> fuelValues = new Object2IntOpenHashMap<>();

    /**
     * constructor also registers event bus, this is not on the mod bus, so doesn't matter which mod calls it
     */
    public ForgeFuelManager() {
        MinecraftForge.EVENT_BUS.addListener(this::onFurnaceFuelBurnTime);
    }

    @Override
    public void addItem(Item item, int fuelPower) {
        if (fuelPower > 0 && item != null) {
            this.fuelValues.put(item, fuelPower);
        }
    }

    /**
     * forge event handler registered in main mod class
     * @param evt forge event
     */
    private void onFurnaceFuelBurnTime(final FurnaceFuelBurnTimeEvent evt) {
        Item item = evt.getItemStack().getItem();
        if (this.fuelValues.containsKey(item)) {
            evt.setBurnTime(this.fuelValues.getInt(item));
        }
    }
}
