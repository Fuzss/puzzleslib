package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

@FunctionalInterface
public interface FinalizeItemComponentsCallback {
    EventInvoker<FinalizeItemComponentsCallback> EVENT = EventInvoker.lookup(FinalizeItemComponentsCallback.class);

    /**
     * Called when default item data components are constructed, allows for applying patches directly.
     *
     * @param item     the item
     * @param consumer apply a patch to the default item data components
     */
    void onFinalizeItemComponents(Item item, Consumer<DataComponentPatch> consumer);
}
