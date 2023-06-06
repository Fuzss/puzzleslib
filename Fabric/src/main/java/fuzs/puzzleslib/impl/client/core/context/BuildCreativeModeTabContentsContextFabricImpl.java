package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuildCreativeModeTabContentsContext;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class BuildCreativeModeTabContentsContextFabricImpl implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        ItemGroupEvents.modifyEntriesEvent(identifier).register(entries -> itemsGenerator.accept(entries.getContext(), entries));
    }

    @Override
    public void registerBuildListener(CreativeModeTab tab, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(tab, "creative mode tab is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> itemsGenerator.accept(entries.getContext(), entries));
    }
}