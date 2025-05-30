package fuzs.puzzleslib.neoforge.api.data.v2.core;

import com.google.common.base.Function;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.data.FileHelperDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
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
     * @param factories all data provider factories to run
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.Factory... factories) {
        registerDataProviders(modId, new RegistrySetBuilder(), factories);
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId              the current mod id
     * @param registrySetBuilder the optional registry set builder
     * @param factories          all data provider factories to run
     */
    public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.Factory... factories) {
        registerDataProviders(modId, registrySetBuilder, factories, (NeoForgeDataProviderContext.Factory factory) -> {
            return (GatherDataEvent evt, CompletableFuture<HolderLookup.Provider> registries) -> {
                return factory.apply(NeoForgeDataProviderContext.fromEvent(modId, evt, registries));
            };
        });
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId     the current mod id
     * @param factories all data provider factories to run
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.LegacyFactory... factories) {
        registerDataProviders(modId, new RegistrySetBuilder(), factories);
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId              the current mod id
     * @param registrySetBuilder the optional registry set builder
     * @param factories          all data provider factories to run
     */
    public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.LegacyFactory... factories) {
        registerDataProviders(modId,
                registrySetBuilder,
                factories,
                (NeoForgeDataProviderContext.LegacyFactory factory) -> {
                    return (GatherDataEvent evt, CompletableFuture<HolderLookup.Provider> registries) -> {
                        return factory.apply(evt, modId);
                    };
                });
    }

    static <T> void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, T[] factories, Function<T, Factory> factoryConverter) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent evt) -> {
                CompletableFuture<HolderLookup.Provider> registries = evt.getLookupProvider();
                if (!registrySetBuilder.getEntryKeys().isEmpty()) {
                    registries = evt.getGenerator()
                            .addProvider(true,
                                    new DatapackBuiltinEntriesProvider(evt.getGenerator().getPackOutput(),
                                            registries,
                                            registrySetBuilder,
                                            Collections.singleton(modId)))
                            .getRegistryProvider();
                }
                for (T factory : factories) {
                    DataProvider dataProvider = factoryConverter.apply(factory).apply(evt, registries);
                    if (dataProvider instanceof RegistriesDataProvider registriesDataProvider) {
                        registries = registriesDataProvider.getRegistries();
                    }
                    if (dataProvider instanceof FileHelperDataProvider fileHelperDataProvider) {
                        fileHelperDataProvider.puzzleslib$setExistingFileHelper(evt.getExistingFileHelper());
                    }
                    evt.getGenerator().addProvider(true, dataProvider);
                }
            });
        });
    }

    @FunctionalInterface
    interface Factory extends BiFunction<GatherDataEvent, CompletableFuture<HolderLookup.Provider>, DataProvider> {
        // NO-OP
    }
}
