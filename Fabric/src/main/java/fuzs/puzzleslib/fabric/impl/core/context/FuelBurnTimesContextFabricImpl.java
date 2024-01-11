package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.FuelBurnTimesContext;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class FuelBurnTimesContextFabricImpl implements FuelBurnTimesContext {

    @Override
    public void registerFuel(int burnTime, ItemLike... items) {
        Preconditions.checkArgument(burnTime >= 0, "burn time is negative");
        Preconditions.checkArgument(burnTime <= 32767, "burn time is too high");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkPositionIndex(1, items.length, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            FuelRegistry.INSTANCE.add(item.asItem(), burnTime);
        }
    }
}
