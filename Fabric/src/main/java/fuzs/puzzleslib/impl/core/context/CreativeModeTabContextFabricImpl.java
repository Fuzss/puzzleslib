package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

public final class CreativeModeTabContextFabricImpl implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTabConfiguratorImpl impl = (CreativeModeTabConfiguratorImpl) configurator;
        FabricItemGroupBuilder.create(impl.getIdentifier()).icon(impl.getIcon()).appendItems((itemStacks, tab) -> {
            impl.getDisplayItemsGenerator().accept(tab, itemStacks::add);
        }).build();
    }
}
