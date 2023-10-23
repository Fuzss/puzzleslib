package fuzs.puzzleslib.api.resources.v1;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A pack resources implementation that is able to dynamically generate contents based on provided {@link net.minecraft.data.DataProvider}s.
 */
public class DynamicPackResources extends AbstractModPackResources {
    /**
     * Helper map for quickly turning a pack type directory back into the {@link PackType}.
     */
    private static final Map<String, PackType> PATHS_FOR_TYPE = Stream.of(PackType.values()).collect(ImmutableMap.toImmutableMap(PackType::getDirectory, Function.identity()));

    /**
     * The {@link net.minecraft.data.DataProvider} factories used by this pack resources instances.
     */
    private final DataProviderContext.Factory[] factories;
    /**
     * A map containing all generated files stored by {@link PackType} and path (in form of a {@link ResourceLocation}).
     */
    protected Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths;

    /**
     * @param factories the {@link net.minecraft.data.DataProvider} factories used by this pack resources instances
     */
    protected DynamicPackResources(DataProviderContext.Factory[] factories) {
        this.factories = factories;
    }

    /**
     * Creates a new dynamic pack resources supplier from privded factories.
     *
     * @param factories data provider factories that execute for dynamically providing all resources when this instance is created
     * @return pack resources supplier
     */
    public static Supplier<AbstractModPackResources> create(DataProviderContext.Factory... factories) {
        return () -> new DynamicPackResources(factories);
    }

    /**
     * Runs all the supplied {@link net.minecraft.data.DataProvider}s,
     * but instead of writing the generated files to disk collects the input streams stored by {@link PackType} and path (in form of a {@link ResourceLocation}).
     *
     * @param modId     the mod id namespace required for the data provider context
     * @param factories the data provider factories
     * @return map containing all generated files
     */
    protected static Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> generatePathsFromProviders(String modId, DataProviderContext.Factory... factories) {
        PuzzlesLib.LOGGER.info("Running data generation for dynamic pack resources provided by {}...", modId);
        DataProviderContext context = DataProviderContext.fromModId(modId);
        try {
            Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> packTypes = Stream.of(PackType.values()).collect(Collectors.toMap(Function.identity(), $ -> Maps.newConcurrentMap()));
            for (DataProviderContext.Factory provider : factories) {
                provider.apply(context).run((Path filePath, byte[] data, HashCode hashCode) -> {
                    List<String> strings = FileUtil.decomposePath(filePath.normalize().toString()).get().left().filter(list -> list.size() >= 2).orElse(null);
                    if (strings != null) {
                        PackType packType = PATHS_FOR_TYPE.get(strings.get(0));
                        Objects.requireNonNull(packType, "pack type for directory %s is null".formatted(strings.get(0)));
                        String path = strings.stream().skip(2).collect(Collectors.joining("/"));
                        ResourceLocation resourceLocation = ResourceLocation.tryBuild(strings.get(1), path);
                        if (resourceLocation != null) {
                            packTypes.get(packType).put(resourceLocation, () -> new ByteArrayInputStream(data));
                        }
                    }
                }).get();
            }
            packTypes.replaceAll((packType, map) -> {
                return ImmutableMap.copyOf(map);
            });
            return Maps.immutableEnumMap(packTypes);
        } catch (Throwable throwable) {
            PuzzlesLib.LOGGER.error("Unable to complete data generation for dynamic pack resources provided by {}", modId, throwable);
            return Map.of();
        }
    }

    @Override
    protected void setup() {
        this.paths = generatePathsFromProviders(this.getNamespace(), this.factories);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        Objects.requireNonNull(this.paths, "paths is null");
        return this.paths.get(packType).get(location);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        Objects.requireNonNull(this.paths, "paths is null");
        this.paths.get(packType).entrySet().stream().filter(entry -> {
            return entry.getKey().getNamespace().equals(namespace) && entry.getKey().getPath().startsWith(path);
        }).forEach(entry -> {
            resourceOutput.accept(entry.getKey(), entry.getValue());
        });
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        Objects.requireNonNull(this.paths, "paths is null");
        return this.paths.get(type).keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
    }
}
