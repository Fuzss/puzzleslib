package fuzs.puzzleslib.api.resources.v1;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.FileUtil;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A pack resources implementation that is able to dynamically generate contents based on provided
 * {@link net.minecraft.data.DataProvider DataProviders}.
 */
public class DynamicPackResources extends AbstractModPackResources {
    /**
     * Helper map for quickly turning a pack type directory back into the {@link PackType}.
     */
    public static final Map<String, PackType> PATHS_FOR_TYPE = Arrays.stream(PackType.values())
            .collect(ImmutableMap.toImmutableMap(PackType::getDirectory, Function.identity()));

    /**
     * The {@link net.minecraft.data.DataProvider} factories used by this pack resources instances.
     */
    protected final DataProviderContext.Factory[] factories;
    /**
     * A map containing all generated files stored by {@link PackType} and path (in form of a
     * {@link ResourceLocation}).
     */
    private Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths;

    /**
     * @param factories the {@link net.minecraft.data.DataProvider} factories used by this pack resources instances
     */
    protected DynamicPackResources(DataProviderContext.Factory... factories) {
        this.factories = factories;
    }

    /**
     * Creates a new dynamic pack resources supplier from privded factories.
     *
     * @param factories data provider factories that execute for dynamically providing all resources when this instance
     *                  is created
     * @return pack resources supplier
     */
    public static Supplier<AbstractModPackResources> create(DataProviderContext.Factory... factories) {
        return () -> new DynamicPackResources(factories);
    }

    /**
     * Runs all the supplied {@link net.minecraft.data.DataProvider}s, but instead of writing the generated files to
     * disk collects the input streams stored by {@link PackType} and path (in form of a {@link ResourceLocation}).
     *
     * @param modId     the mod id namespace required for the data provider context
     * @param factories the data provider factories
     * @return map containing all generated files
     */
    public static Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> generatePathsFromProviders(String modId, DataProviderContext.Factory... factories) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            // start with a map for each type, as data generation below runs on a thread pool,
            // they will override each other when creating the maps
            Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths = Arrays.stream(PackType.values())
                    .collect(Collectors.toMap(Function.identity(), (PackType packType) -> new ConcurrentHashMap<>()));
            DataProviderContext context = DataProviderContext.ofPath(modId);
            for (DataProviderContext.Factory factory : factories) {
                DataProvider dataProvider = factory.apply(context);
                if (dataProvider instanceof RegistriesDataProvider registriesDataProvider) {
                    context = context.withRegistries(registriesDataProvider.getRegistries());
                }
                dataProvider.run((Path filePath, byte[] data, HashCode hashCode) -> {
                    // good times with Windows...
                    List<String> strings = FileUtil.decomposePath(filePath.normalize()
                            .toString()
                            .replace(File.separator, "/")).result().filter(list -> list.size() >= 2).orElse(null);
                    if (strings != null) {
                        PackType packType = PATHS_FOR_TYPE.get(strings.getFirst());
                        Objects.requireNonNull(packType,
                                () -> "pack type for directory %s is null".formatted(strings.getFirst()));
                        String path = strings.stream().skip(2).collect(Collectors.joining("/"));
                        ResourceLocation resourceLocation = ResourceLocation.tryBuild(strings.get(1), path);
                        if (resourceLocation != null) {
                            paths.get(packType).put(resourceLocation, () -> new ByteArrayInputStream(data));
                        }
                    }
                }).join();
            }
            Iterator<Map.Entry<PackType, Map<ResourceLocation, IoSupplier<InputStream>>>> iterator = paths.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Map.Entry<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> entry = iterator.next();
                if (!entry.getValue().isEmpty()) {
                    entry.setValue(ImmutableMap.copyOf(entry.getValue()));
                } else {
                    iterator.remove();
                }
            }
            PuzzlesLib.LOGGER.info("Data generation for dynamic pack resources provided by '{}' took {}ms",
                    modId,
                    stopwatch.stop().elapsed().toMillis());
            return Maps.immutableEnumMap(paths);
        } catch (Throwable throwable) {
            PuzzlesLib.LOGGER.warn("Unable to complete data generation for dynamic pack resources provided by '{}'",
                    modId,
                    throwable);
            return Collections.emptyMap();
        }
    }

    protected Map<ResourceLocation, IoSupplier<InputStream>> getPathsForType(PackType packType) {
        Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths = this.paths;
        if (paths == null) {
            paths = this.paths = this.generatePathsFromProviders();
        }
        return paths.getOrDefault(packType, Collections.emptyMap());
    }

    protected Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> generatePathsFromProviders() {
        return generatePathsFromProviders(this.getNamespace(), this.factories);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        return this.getPathsForType(packType).get(location);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        this.getPathsForType(packType)
                .entrySet()
                .stream()
                .filter((Map.Entry<ResourceLocation, IoSupplier<InputStream>> entry) -> {
                    return entry.getKey().getNamespace().equals(namespace) && entry.getKey().getPath().startsWith(path);
                })
                .forEach((Map.Entry<ResourceLocation, IoSupplier<InputStream>> entry) -> {
                    resourceOutput.accept(entry.getKey(), entry.getValue());
                });
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return this.getPathsForType(packType)
                .keySet()
                .stream()
                .map(ResourceLocation::getNamespace)
                .collect(Collectors.toSet());
    }
}
