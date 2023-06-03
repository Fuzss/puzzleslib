package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.world.item.CreativeModeTab;

@FunctionalInterface
public interface BuildCreativeContentsCallback {
    EventInvoker<BuildCreativeContentsCallback> EVENT = EventInvoker.lookup(BuildCreativeContentsCallback.class);

    /**
     * Called whenever the displayed items in {@link net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen} are rebuilt,
     * allows for adding custom items to the end of a tab.
     *
     * @param tab    the {@link CreativeModeTab} item contents are being built for
     * @param output a consumer for appending additional contents
     */
    void onBuildCreativeContents(CreativeModeTab tab, DisplayItemsOutput output);
}
