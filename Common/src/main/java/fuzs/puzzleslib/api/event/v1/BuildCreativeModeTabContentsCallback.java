package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface BuildCreativeModeTabContentsCallback {

    static EventInvoker<BuildCreativeModeTabContentsCallback> buildCreativeModeTabContents(ResourceKey<CreativeModeTab> resourceKey) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        return EventInvoker.lookup(BuildCreativeModeTabContentsCallback.class, resourceKey);
    }

    /**
     * Called every time an existing creative mode tab is rebuilt. Allows for adding new display items, removals are not
     * supported.
     * <p>
     * For creating brand-new creative mode tabs see
     * {@link fuzs.puzzleslib.api.init.v3.registry.RegistryManager#registerCreativeModeTab(String, Consumer)}.
     *
     * @param creativeModeTab       the creative mode tab instance
     * @param itemDisplayParameters the item display parameters
     * @param output                the display items consumer
     */
    void onBuildCreativeModeTabContents(CreativeModeTab creativeModeTab, CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output);
}
