package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.client.event.v1.BuildCreativeContentsCallback;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class CreativeModeTabContextFabricImpl implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTabConfiguratorImpl impl = (CreativeModeTabConfiguratorImpl) configurator;
        CreativeModeTab tab = FabricItemGroupBuilder.create(impl.getIdentifier()).icon(impl.getIcon()).appendItems((itemStacks, tab1) -> {
            // this is handled by the event below to properly support the way the search tab is implemented (it fires for every possible tab instead of the search tab itself)
            // since there is no way to differentiate between the search tab and this tab being populated fillItemList on the tab itself is skipped
        }).build();
        BuildCreativeContentsCallback.EVENT.register((identifier, tab1, output) -> {
            if (tab1 == tab) impl.getDisplayItemsGenerator().accept(output);
        });
    }
}
