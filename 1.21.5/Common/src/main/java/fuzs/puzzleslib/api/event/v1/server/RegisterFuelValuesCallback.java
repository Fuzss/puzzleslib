package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.level.block.entity.FuelValues;

@Deprecated(forRemoval = true)
@FunctionalInterface
public interface RegisterFuelValuesCallback {
    EventInvoker<RegisterFuelValuesCallback> EVENT = EventInvoker.lookup(RegisterFuelValuesCallback.class);

    /**
     * Allows for registering items as furnace fuel.
     *
     * @param builder       the fuel values builder, vanilla values are not contained on NeoForge
     * @param fuelBaseValue the fuel base value unit used as a multiplier
     */
    void onRegisterFuelValues(FuelValues.Builder builder, int fuelBaseValue);
}
