package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated replaced by {@link fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext}
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface BuildCreativeContentsCallback {
    EventInvoker<BuildCreativeContentsCallback> EVENT = (EventPhase phase, BuildCreativeContentsCallback callback) -> {};

    /**
     * Called whenever the displayed items in {@link net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen} are rebuilt,
     * allows for adding custom items to the end of a tab.
     *
     * @param tab    the {@link CreativeModeTab} item contents are being built for
     * @param output a consumer for appending additional contents
     */
    void onBuildCreativeContents(@Nullable ResourceLocation identifier, CreativeModeTab tab, DisplayItemsOutput output);
}
