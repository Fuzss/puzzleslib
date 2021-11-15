package fuzs.puzzleslib.registry;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * applies fuel burn times instead of implementing this on the item side
 * heavily inspired by FuelHandler found in Vazkii's Quark mod
 */
public enum FuelManager {
    /**
     * instance holder for lazy and thread-safe initialization
     */
    INSTANCE;

    /**
     * stored burn times
     */
    private final Object2IntOpenHashMap<Item> fuelValues = new Object2IntOpenHashMap<>();

    FuelManager() {
        MinecraftForge.EVENT_BUS.addListener(this::onFurnaceFuelBurnTime);
    }

    /**
     * @param item item to add
     * @param fuelPower burn time
     */
    public void addItem(Item item, int fuelPower) {
        if (fuelPower > 0 && item != null) {
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
    private void onFurnaceFuelBurnTime(final FurnaceFuelBurnTimeEvent evt) {
        Item item = evt.getItemStack().getItem();
        if (this.fuelValues.containsKey(item)) {
            evt.setBurnTime(this.fuelValues.getInt(item));
        }
    }
}
