package fuzs.puzzleslib.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

/**
 * applies fuel burn times instead of implementing this on the item side
 * heavily inspired by FuelHandler found in Vazkii's Quark mod
 */
public interface FuelManager {

    /**
     * @param item item to add
     * @param fuelPower burn time
     */
    void addItem(Item item, int fuelPower);

    /**
     * @param block block to add
     * @param fuelPower burn time
     */
    default void addBlock(Block block, int fuelPower) {
        this.addItem(block.asItem(), fuelPower);
    }

    /**
     * add wooden block with default vanilla times
     * @param block block to add
     */
    default void addWoodenBlock(Block block) {
        this.addBlock(block, block instanceof SlabBlock ? 150 : 300);
    }
}
