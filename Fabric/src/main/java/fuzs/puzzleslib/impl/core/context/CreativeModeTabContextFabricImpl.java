package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

public final class CreativeModeTabContextFabricImpl implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        ((CreativeModeTabConfiguratorImpl) configurator).configure(FabricItemGroup.builder(((CreativeModeTabConfiguratorImpl) configurator).getIdentifier()));
    }
}
