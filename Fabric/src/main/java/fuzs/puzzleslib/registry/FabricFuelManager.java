package fuzs.puzzleslib.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;

/**
 * define fuel burn times on Fabric
 */
public class FabricFuelManager implements FuelManager {

    @Override
    public void addItem(Item item, int fuelPower) {
        if (fuelPower > 0 && item != null) {
            FuelRegistry.INSTANCE.add(item, fuelPower);
        }
    }
}
