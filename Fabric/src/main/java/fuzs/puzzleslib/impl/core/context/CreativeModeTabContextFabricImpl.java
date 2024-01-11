package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.world.item.CreativeModeTab;

public final class CreativeModeTabContextFabricImpl implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTabConfiguratorImpl impl = (CreativeModeTabConfiguratorImpl) configurator;
        CreativeModeTab tab = FabricItemGroupBuilder.create(impl.getIdentifier()).icon(impl.getIcon()).appendItems((items, $) -> {
            impl.getDisplayItemsGenerator().accept(items::add);
        }).build();
        impl.getDisplayItemsGenerator().accept(DisplayItemsOutput.setItemCategory(tab));
    }
}
