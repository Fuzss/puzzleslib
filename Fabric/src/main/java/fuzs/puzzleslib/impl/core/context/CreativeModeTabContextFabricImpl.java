package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;

public final class CreativeModeTabContextFabricImpl implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTab.Builder builder = FabricItemGroup.builder(((CreativeModeTabConfiguratorImpl) configurator).getIdentifier());
        ((CreativeModeTabConfiguratorImpl) configurator).configure(builder);
        builder.build();
    }
}
