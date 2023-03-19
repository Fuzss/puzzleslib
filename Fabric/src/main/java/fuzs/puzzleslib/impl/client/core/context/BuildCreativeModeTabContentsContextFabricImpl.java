package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuildCreativeModeTabContentsContext;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class BuildCreativeModeTabContentsContextFabricImpl implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(displayItemsGenerator, "display items generator is null");
        ItemGroupEvents.modifyEntriesEvent(identifier).register(entries -> displayItemsGenerator.accept(entries.getEnabledFeatures(), entries, entries.shouldShowOpRestrictedItems()));
    }

    @Override
    public void registerBuildListener(CreativeModeTab creativeModeTab, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
        Objects.requireNonNull(displayItemsGenerator, "display items generator is null");
        ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register(entries -> displayItemsGenerator.accept(entries.getEnabledFeatures(), entries, entries.shouldShowOpRestrictedItems()));
    }
}
