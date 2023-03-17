package fuzs.puzzleslib.impl.core.contexts;

import fuzs.puzzleslib.api.core.v1.contexts.FuelBurnTimesContext;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class FuelBurnTimesContextFabricImpl implements FuelBurnTimesContext {

    @Override
    public void registerFuel(int burnTime, ItemLike item, ItemLike... items) {
        if (Mth.clamp(burnTime, 1, 32767) != burnTime)
            throw new IllegalArgumentException("fuel burn time is out of bounds");
        Objects.requireNonNull(item, "item is null");
        FuelRegistry.INSTANCE.add(item.asItem(), burnTime);
        Objects.requireNonNull(items, "items is null");
        for (ItemLike other : items) {
            Objects.requireNonNull(other, "item is null");
            FuelRegistry.INSTANCE.add(other.asItem(), burnTime);
        }
    }
}
