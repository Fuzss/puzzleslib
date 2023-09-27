package fuzs.puzzleslib.api.data.v2.core;

import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.data.ExistingFileHelperHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Objects;

public final class DataProviderHelper {

    private DataProviderHelper() {

    }

    public static void registerDataProviders(String modId, DataProviderContext.Factory... factories) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        Objects.checkIndex(0, factories.length);
        ModContainerHelper.findModEventBus(modId).ifPresent(eventBus -> {
            eventBus.addListener((final GatherDataEvent evt) -> {
                onGatherData(evt, modId, factories);
            });
        });
    }

    static void onGatherData(GatherDataEvent evt, String modId, DataProviderContext.Factory... factories) {
        Objects.checkIndex(0, factories.length);
        DataProviderContext context = ForgeDataProviderContext.fromEvent(modId, evt);
        DataGenerator dataGenerator = evt.getGenerator();
        for (DataProviderContext.Factory factory : factories) {
            DataProvider dataProvider = factory.apply(context);
            if (dataProvider instanceof ExistingFileHelperHolder holder) {
                holder.puzzleslib$setExistingFileHelper(evt.getExistingFileHelper());
            }
            dataGenerator.addProvider(true, dataProvider);
        }
    }
}
