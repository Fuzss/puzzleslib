package fuzs.puzzleslib.api.data.v2.core;

import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.data.ExistingFileHelperHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * A helper class for registering {@link DataProvider} instances which run during data-gen in a development environment.
 */
public final class DataProviderHelper {

    private DataProviderHelper() {

    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen (when {@link GatherDataEvent} fires).
     *
     * @param modId     the current mod id
     * @param factories all data provider factories to run
     */
    public static void registerDataProviders(String modId, ForgeDataProviderContext.Factory... factories) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        Objects.checkIndex(0, factories.length);
        registerDataProviders(modId, Stream.of(factories).map(factory -> {
            return (ForgeDataProviderContext.LegacyFactory) (GatherDataEvent evt, String $) -> {
                return factory.apply(ForgeDataProviderContext.fromEvent(modId, evt));
            };
        }).toArray(ForgeDataProviderContext.LegacyFactory[]::new));
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen (when {@link GatherDataEvent} fires).
     *
     * @param modId     the current mod id
     * @param factories all data provider factories to run
     */
    public static void registerDataProviders(String modId, ForgeDataProviderContext.LegacyFactory... factories) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        Objects.checkIndex(0, factories.length);
        ModContainerHelper.getOptionalModEventBus(modId).ifPresent(eventBus -> {
            eventBus.addListener((final GatherDataEvent evt) -> {
                onGatherData(evt, modId, factories);
            });
        });
    }

    /**
     * Implementation of the actual {@link GatherDataEvent}.
     *
     * @param evt       the data-gen event
     * @param modId     the current mod id
     * @param factories all data provider factories to run
     */
    static void onGatherData(GatherDataEvent evt, String modId, ForgeDataProviderContext.LegacyFactory... factories) {
        Objects.checkIndex(0, factories.length);
        DataGenerator dataGenerator = evt.getGenerator();
        for (ForgeDataProviderContext.LegacyFactory factory : factories) {
            DataProvider dataProvider = factory.apply(evt, modId);
            if (dataProvider instanceof ExistingFileHelperHolder holder) {
                holder.puzzleslib$setExistingFileHelper(evt.getExistingFileHelper());
            }
            dataGenerator.addProvider(true, dataProvider);
        }
    }
}
