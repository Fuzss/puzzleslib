package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.FuelBurnTimesContext;
import fuzs.puzzleslib.api.core.v1.context.MultiRegistrationContext;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.level.ItemLike;

public final class FuelBurnTimesContextFabricImpl implements FuelBurnTimesContext, MultiRegistrationContext<ItemLike, Integer> {

    @Override
    public void registerFuel(int burnTime, ItemLike item, ItemLike... items) {
        if (burnTime <= 0) throw new IllegalArgumentException("burn time must be greater than 0");
        this.register(burnTime, item, items);
    }

    @Override
    public void register(ItemLike object, Integer type) {
        FuelRegistry.INSTANCE.add(object.asItem(), type);
    }
}
