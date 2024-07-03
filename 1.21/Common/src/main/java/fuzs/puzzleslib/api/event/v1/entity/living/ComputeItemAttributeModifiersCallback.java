package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

@FunctionalInterface
public interface ComputeItemAttributeModifiersCallback {
    EventInvoker<ComputeItemAttributeModifiersCallback> EVENT = EventInvoker.lookup(
            ComputeItemAttributeModifiersCallback.class);

    /**
     * Called when {@link ItemAttributeModifiers} are being queried for an {@link ItemStack}.
     *
     * @param item                   the item attribute modifiers are being queried for
     * @param itemAttributeModifiers the item attribute modifiers
     */
    void onComputeItemAttributeModifiers(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers);
}
