package fuzs.puzzleslib.neoforge.api.data.v2.core;

import com.google.common.base.Function;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.data.v2.ModPackMetadataProvider;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A helper class for registering {@link DataProvider DataProviders} which run during data generation in a development
 * environment.
 */
public final class DataProviderHelper {

    private DataProviderHelper() {
        // NO-OP
    }

    /**
     * Register {@link DataProvider DataProviders} to be run during data generation via {@link GatherDataEvent}.
     *
     * @param modId                 the mod id
     * @param dataProviderFactories the data provider factories
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
        registerDataProviders(modId, new RegistrySetBuilder(), dataProviderFactories);
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId                 the current mod id
     * @param dataProviderFactories all data provider factories to run
     */
    public static void registerDataProviders(String modId, NeoForgeDataProviderContext.LegacyFactory... dataProviderFactories) {
        registerDataProviders(modId, new RegistrySetBuilder(), dataProviderFactories);
    }

    /**
     * Register {@link DataProvider DataProviders} for a built-in pack bundled with the mod to be run during data
     * generation via {@link GatherDataEvent}.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param resourceLocation      the resource location
     * @param packType              the pack type
     * @param dataProviderFactories the data provider factories to run
     */
    public static void registerDataProviders(ResourceLocation resourceLocation, PackType packType, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
        registerDataProviders(resourceLocation, packType, new RegistrySetBuilder(), dataProviderFactories);
    }

    /**
     * Register {@link DataProvider DataProviders} to be run during data generation via {@link GatherDataEvent}.
     *
     * @param modId                 the mod id
     * @param registrySetBuilder    the optional registry set builder
     * @param dataProviderFactories the data provider factories
     */
    public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
        registerDataProviders(modId,
                registrySetBuilder,
                dataProviderFactories,
                (NeoForgeDataProviderContext.Factory factory) -> {
                    return (GatherDataEvent event, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) -> {
                        return factory.apply(NeoForgeDataProviderContext.fromEvent(event, packOutput, lookupProvider));
                    };
                });
    }

    /**
     * Registers factories for multiple {@link DataProvider} instances to be run during data-gen, which is when
     * {@link GatherDataEvent} fires.
     *
     * @param modId                 the current mod id
     * @param registrySetBuilder    the optional registry set builder
     * @param dataProviderFactories all data provider factories to run
     */
    public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.LegacyFactory... dataProviderFactories) {
        registerDataProviders(modId,
                registrySetBuilder,
                dataProviderFactories,
                (NeoForgeDataProviderContext.LegacyFactory factory) -> {
                    return (GatherDataEvent evt, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) -> {
                        return factory.apply(evt, modId);
                    };
                });
    }

    /**
     * Register {@link DataProvider DataProviders} for a built-in pack bundled with the mod to be run during data
     * generation via {@link GatherDataEvent}.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param resourceLocation      the resource location
     * @param packType              the pack type
     * @param registrySetBuilder    the optional registry set builder
     * @param dataProviderFactories the data provider factories to run
     */
    public static void registerDataProviders(ResourceLocation resourceLocation, PackType packType, RegistrySetBuilder registrySetBuilder, NeoForgeDataProviderContext.Factory... dataProviderFactories) {
        registerDataProviders(resourceLocation,
                packType,
                registrySetBuilder,
                ArrayUtils.add(dataProviderFactories, (NeoForgeDataProviderContext context) -> {
                    return new ModPackMetadataProvider(packType, context);
                }),
                (NeoForgeDataProviderContext.Factory factory) -> {
                    return (GatherDataEvent event, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) -> {
                        return factory.apply(NeoForgeDataProviderContext.fromEvent(event, packOutput, lookupProvider));
                    };
                });
    }

    private static <T> void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, T[] dataProviderFactories, Function<T, Factory> factoryConverter) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent event) -> {
                addDataProviders(event,
                        registrySetBuilder,
                        dataProviderFactories,
                        factoryConverter,
                        event.getGenerator().getPackOutput(),
                        (DataProvider dataProvider) -> event.getGenerator().addProvider(true, dataProvider));
            });
        });
    }

    private static <T> void registerDataProviders(ResourceLocation resourceLocation, PackType packType, RegistrySetBuilder registrySetBuilder, T[] dataProviderFactories, Function<T, Factory> factoryConverter) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace())
                .ifPresent((IEventBus eventBus) -> {
                    eventBus.addListener((final GatherDataEvent event) -> {
                        Path path = event.getGenerator().getPackOutput().getOutputFolder();
                        PackOutput packOutput = new PackOutput(event.getGenerator()
                                .getPackOutput()
                                .getOutputFolder()
                                .resolve(packType.getDirectory())
                                .resolve(resourceLocation.getNamespace())
                                .resolve(packType == PackType.CLIENT_RESOURCES ? "resourcepacks" : "datapacks")
                                .resolve(resourceLocation.getPath()));
                        DataGenerator.PackGenerator packGenerator = event.getGenerator().new PackGenerator(true,
                                resourceLocation.toString(),
                                event.getGenerator()
                                        .getPackOutput(path.relativize(packOutput.getOutputFolder()).toString()));
                        addDataProviders(event,
                                registrySetBuilder,
                                dataProviderFactories,
                                factoryConverter,
                                packOutput,
                                (DataProvider dataProvider) -> {
                                    packGenerator.addProvider((PackOutput packOutputX) -> dataProvider);
                                });
                    });
                });
    }

    private static <T> void addDataProviders(GatherDataEvent event, RegistrySetBuilder registrySetBuilder, T[] dataProviderFactories, Function<T, Factory> factoryConverter, PackOutput packOutput, Consumer<DataProvider> dataProviderConsumer) {
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (!registrySetBuilder.getEntryKeys().isEmpty()) {
            lookupProvider = event.getGenerator()
                    .addProvider(true,
                            new DatapackBuiltinEntriesProvider(event.getGenerator().getPackOutput(),
                                    lookupProvider,
                                    registrySetBuilder,
                                    Collections.singleton(event.getModContainer().getModId())))
                    .getRegistryProvider();
        }
        for (T dataProviderFactory : dataProviderFactories) {
            DataProvider dataProvider = factoryConverter.apply(dataProviderFactory)
                    .apply(event, packOutput, lookupProvider);
            if (dataProvider instanceof RegistriesDataProvider registriesDataProvider) {
                lookupProvider = registriesDataProvider.getRegistries();
            }
            dataProviderConsumer.accept(dataProvider);
        }
    }

    @FunctionalInterface
    private interface Factory {

        DataProvider apply(GatherDataEvent event, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider);
    }
}
