package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;

public final class CreativeTabContentsContextFabricImpl implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceKey<CreativeModeTab> resourceKey, CreativeModeTab.DisplayItemsGenerator displayItems) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        Objects.requireNonNull(displayItems, "display items generator is null");
        ItemGroupEvents.modifyEntriesEvent(resourceKey).register(entries -> displayItems.accept(entries.getContext(), entries));
    }
}
