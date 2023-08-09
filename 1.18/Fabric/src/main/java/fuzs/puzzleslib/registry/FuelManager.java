package fuzs.puzzleslib.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

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
     * @param item item to add
     * @param fuelPower burn time
     */
    public void addItem(Item item, int fuelPower) {
        if (fuelPower > 0 && item != null) {
            FuelRegistry.INSTANCE.add(item, fuelPower);
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
}
