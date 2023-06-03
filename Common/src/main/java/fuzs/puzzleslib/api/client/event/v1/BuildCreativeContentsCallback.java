package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

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
    void onBuildCreativeContents(@Nullable ResourceLocation identifier, CreativeModeTab tab, DisplayItemsOutput output);

    @ApiStatus.Internal
    @Nullable
    static ResourceLocation tryCreateIdentifier(CreativeModeTab tab) {
        return ResourceLocation.tryParse(tab.getRecipeFolderName().toLowerCase(Locale.ROOT).replace(".", ":"));
    }
}
