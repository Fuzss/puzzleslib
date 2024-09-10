package fuzs.puzzleslib.neoforge.api.data.v2.core;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.data.ExistingFileHelperHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.stream.Stream;

/**
 * A helper class for registering {@link DataProvider} instances which run during data-gen in a development environment.
 */
public final class DataProviderHelper {

    private DataProviderHelper() {
        // NO-OP
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen (when {@link GatherDataEvent} fires).
     *
     * @param modId     the current mod id
     * @param factories all data provider factories to run
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.Factory... factories) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        Preconditions.checkState(factories.length > 0, "data provider factories is empty");
        registerDataProviders(modId, Stream.of(factories).map(factory -> {
            return (NeoForgeDataProviderContext.LegacyFactory) (GatherDataEvent evt, String $) -> {
                return factory.apply(NeoForgeDataProviderContext.fromEvent(modId, evt));
            };
        }).toArray(NeoForgeDataProviderContext.LegacyFactory[]::new));
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen (when {@link GatherDataEvent} fires).
     *
     * @param modId     the current mod id
     * @param factories all data provider factories to run
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.LegacyFactory... factories) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        Preconditions.checkState(factories.length > 0, "data provider factories is empty");
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent(eventBus -> {
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
    static void onGatherData(GatherDataEvent evt, String modId, NeoForgeDataProviderContext.LegacyFactory... factories) {
        Preconditions.checkState(factories.length > 0, "data provider factories is empty");
        DataGenerator dataGenerator = evt.getGenerator();
        for (NeoForgeDataProviderContext.LegacyFactory factory : factories) {
            DataProvider dataProvider = factory.apply(evt, modId);
            if (dataProvider instanceof ExistingFileHelperHolder holder) {
                holder.puzzleslib$setExistingFileHelper(evt.getExistingFileHelper());
            }
            dataGenerator.addProvider(true, dataProvider);
        }
    }
}
