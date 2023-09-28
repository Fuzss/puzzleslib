package fuzs.puzzleslib.api.data.v2.core;

import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.data.ExistingFileHelperHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Objects;

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
        ModContainerHelper.findModEventBus(modId).ifPresent(eventBus -> {
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
    static void onGatherData(GatherDataEvent evt, String modId, ForgeDataProviderContext.Factory... factories) {
        Objects.checkIndex(0, factories.length);
        ForgeDataProviderContext context = ForgeDataProviderContext.fromEvent(modId, evt);
        DataGenerator dataGenerator = evt.getGenerator();
        for (ForgeDataProviderContext.Factory factory : factories) {
            DataProvider dataProvider = factory.apply(context);
            if (dataProvider instanceof ExistingFileHelperHolder holder) {
                holder.puzzleslib$setExistingFileHelper(evt.getExistingFileHelper());
            }
            dataGenerator.addProvider(true, dataProvider);
        }
    }
}
