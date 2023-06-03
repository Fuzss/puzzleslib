package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;

/**
 * Register new creative mode tabs via the respective builder.
 */
@FunctionalInterface
public interface CreativeModeTabContext {

    /**
     * Register a {@link CreativeModeTabConfigurator} which is used to configure a {@link net.minecraft.world.item.CreativeModeTab}
     *
     * @param configurator the configurator instance
     */
    void registerCreativeModeTab(CreativeModeTabConfigurator configurator);
}
