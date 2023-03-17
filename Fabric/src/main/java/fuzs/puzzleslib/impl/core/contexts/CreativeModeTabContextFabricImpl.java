package fuzs.puzzleslib.impl.core.contexts;

import fuzs.puzzleslib.api.core.v1.contexts.CreativeModeTabContext;
import fuzs.puzzleslib.api.creativemodetab.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.creativemodetab.CreativeModeTabConfiguratorImpl;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

public final class CreativeModeTabContextFabricImpl implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        ((CreativeModeTabConfiguratorImpl) configurator).configure(FabricItemGroup.builder(((CreativeModeTabConfiguratorImpl) configurator).getIdentifier()));
    }
}
