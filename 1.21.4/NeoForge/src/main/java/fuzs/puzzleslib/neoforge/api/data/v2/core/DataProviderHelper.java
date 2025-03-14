package fuzs.puzzleslib.neoforge.api.data.v2.core;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

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
    @Deprecated(forRemoval = true)
    public static void registerDataProviders(String modId, DataProviderContext.Factory... factories) {
        registerDataProviders(modId, new RegistrySetBuilder(), factories, (DataProviderContext.Factory factory) -> {
            return (GatherDataEvent evt, CompletableFuture<HolderLookup.Provider> registries) -> {
                return factory.apply(NeoForgeDataProviderContext.fromEvent(evt, registries));
            };
        });
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId     the current mod id
     * @param factories the data provider factories to run
     */
    public static void registerDataProvidersV2(String modId, NeoForgeDataProviderContext.Factory... factories) {
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
        registerDataProviders(modId, registrySetBuilder, factories, (NeoForgeDataProviderContext.Factory factory) -> {
            return (GatherDataEvent evt, CompletableFuture<HolderLookup.Provider> registries) -> {
                return factory.apply(NeoForgeDataProviderContext.fromEvent(evt, registries));
            };
        });
    }

    static <T> void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, T[] factories, Function<T, Factory> factoryConverter) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        Preconditions.checkState(factories.length > 0, "data provider factories is empty");
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent.Client evt) -> {
                if (!registrySetBuilder.getEntryKeys().isEmpty()) {
                    evt.createDatapackRegistryObjects(registrySetBuilder);
                }
                onGatherData(evt, Arrays.stream(factories).map(factoryConverter).toList());
            });
        });
    }

    static void onGatherData(GatherDataEvent evt, Collection<Factory> factories) {
        CompletableFuture<HolderLookup.Provider> registries = evt.getLookupProvider();
        for (Factory factory : factories) {
            DataProvider dataProvider = factory.apply(evt, registries);
            if (dataProvider instanceof RegistriesDataProvider registriesDataProvider) {
                registries = registriesDataProvider.getRegistries();
            }
            evt.addProvider(dataProvider);
        }
    }

    @FunctionalInterface
    interface Factory extends BiFunction<GatherDataEvent, CompletableFuture<HolderLookup.Provider>, DataProvider> {
        // NO-OP
    }
}
