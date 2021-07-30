package fuzs.puzzleslib.registry;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.Item;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;

/**
 * applies fuel burn times instead of implementing this on the item side
 * heavily inspired by FuelHandler found in Vazkii's Quark mod
 */
public class FuelManager {

    /**
     * stored burn times
     */
    private final Object2IntOpenHashMap<Item> fuelValues = new Object2IntOpenHashMap<>();

    /**
     * private singleton constructor
     */
    private FuelManager() {

    }

    /**
     * @param item item to add
     * @param fuelPower burn time
     */
    public void addItem(Item item, int fuelPower) {

        if(fuelPower > 0 && item != null) {

            this.fuelValues.put(item, fuelPower);
        }
    }

    /**
     * @param block block to add
     * @param fuelPower burn time
     */
    public void addBlock(Block block, int fuelPower) {

        this.addItem(block.asItem(), fuelPower);
    }

    /**
     * add wooden block with default vanilla times
     * @param block block to add
     */
    public void addWoodenBlock(Block block) {

        this.addBlock(block, block instanceof SlabBlock ? 150 : 300);
    }

    /**
     * forge event handler registered in main mod class
     * @param evt forge event
     */
    public void onFurnaceFuelBurnTime(final FurnaceFuelBurnTimeEvent evt) {

        Item item = evt.getItemStack().getItem();
        if(this.fuelValues.containsKey(item)) {

            evt.setBurnTime(this.fuelValues.getInt(item));
        }
    }

    /**
     * @return {@link FuelManager} instance
     */
    public static FuelManager getInstance() {

        return FuelManager.FuelManagerHolder.INSTANCE;
    }

    /**
     * instance holder class for lazy and thread-safe initialization
     */
    private static class FuelManagerHolder {

        private static final FuelManager INSTANCE = new FuelManager();

    }

}
