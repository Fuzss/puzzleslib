package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.FuelBurnTimesContext;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

import java.util.Objects;

public final class FuelBurnTimesContextNeoForgeImpl implements FuelBurnTimesContext {
    private final Object2IntOpenHashMap<Item> fuelBurnTimes = new Object2IntOpenHashMap<>();

    @Override
    public void registerFuel(int burnTime, ItemLike... items) {
        Preconditions.checkArgument(burnTime >= 0, "burn time is negative");
        Preconditions.checkArgument(burnTime <= 32767, "burn time is too high");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        if (this.fuelBurnTimes.isEmpty()) {
            NeoForge.EVENT_BUS.addListener(this::onFurnaceFuelBurnTime);
        }
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            this.fuelBurnTimes.put(item.asItem(), burnTime);
        }
    }

    private void onFurnaceFuelBurnTime(final FurnaceFuelBurnTimeEvent evt) {
        Item item = evt.getItemStack().getItem();
        if (this.fuelBurnTimes.containsKey(item)) {
            evt.setBurnTime(this.fuelBurnTimes.getInt(item));
        }
    }
}
