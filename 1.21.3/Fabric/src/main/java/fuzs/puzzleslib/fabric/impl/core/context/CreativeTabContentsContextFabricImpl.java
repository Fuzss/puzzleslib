package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;

public final class CreativeTabContentsContextFabricImpl implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceKey<CreativeModeTab> resourceKey, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        ItemGroupEvents.modifyEntriesEvent(resourceKey).register(entries -> itemsGenerator.accept(entries.getContext(), entries));
    }
}
