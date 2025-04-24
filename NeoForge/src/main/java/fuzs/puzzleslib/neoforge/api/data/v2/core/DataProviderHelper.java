package fuzs.puzzleslib.neoforge.api.data.v2.core;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * A helper class for registering {@link DataProvider} instances which run during data-gen in a development
 * environment.
 */
public final class DataProviderHelper {

    private DataProviderHelper() {
        // NO-OP
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId     the current mod id
     * @param factories the data provider factories to run
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.Factory... factories) {
        registerDataProviders(modId, new RegistrySetBuilder(), factories);
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId     the current mod id
     * @param factories the data provider factories to run
     */
    public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.Factory... factories) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent.Client evt) -> {
                if (!registrySetBuilder.getEntryKeys().isEmpty()) {
                    evt.createDatapackRegistryObjects(registrySetBuilder);
                }
                for (NeoForgeDataProviderContext.Factory factory : factories) {
                    evt.addProvider(factory.apply(NeoForgeDataProviderContext.fromEvent(evt)));
                }
            });
        });
    }
}
