package fuzs.puzzleslib.api.core.v1.contexts;

import fuzs.puzzleslib.api.creativemodetab.v2.CreativeModeTabConfigurator;

/**
 * Register new creative mode tabs via the respective builder.
 */
@FunctionalInterface
public interface CreativeModeTabContext {

    /**
     * Register a {@link CreativeModeTabConfigurator} which is used to configure a {@link net.minecraft.world.item.CreativeModeTab.Builder}
     *
     * @param configurator the configurator instance
     */
    void registerCreativeModeTab(CreativeModeTabConfigurator configurator);
}
