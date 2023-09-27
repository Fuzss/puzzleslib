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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicPackResources extends AbstractModPackResources {
    private static final Map<String, PackType> PATHS_FOR_TYPE = Stream.of(PackType.values()).collect(ImmutableMap.toImmutableMap(PackType::getDirectory, Function.identity()));

    private final Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths;

    protected DynamicPackResources(Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> paths) {
        this.paths = paths;
    }

    public static Supplier<AbstractModPackResources> create(String modId, DataProviderContext.Factory... factories) {
        return () -> {
            DataProviderContext context = DataProviderContext.fromModId(modId);
            return new DynamicPackResources(generatePathsFromProviders(context, factories));
        };
    }

    protected static Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> generatePathsFromProviders(DataProviderContext context, DataProviderContext.Factory... factories) {
        try {
            Map<PackType, Map<ResourceLocation, IoSupplier<InputStream>>> packTypes = Stream.of(PackType.values()).collect(Collectors.toMap(Function.identity(), $ -> Maps.newConcurrentMap()));
            for (DataProviderContext.Factory provider : factories) {
                provider.apply(context).run((Path filePath, byte[] data, HashCode hashCode) -> {
                    List<String> strings = FileUtil.decomposePath(filePath.normalize().toString()).get().left().filter(list -> list.size() >= 2).orElse(null);
                    if (strings != null) {
                        PackType packType = PATHS_FOR_TYPE.get(strings.get(0));
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
            PuzzlesLib.LOGGER.error("Unable to construct dynamic pack resources", throwable);
            return Map.of();
        }
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        return this.paths.get(packType).get(location);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        this.paths.get(packType).entrySet().stream().filter(entry -> {
            return entry.getKey().getNamespace().equals(namespace) && entry.getKey().getPath().startsWith(path);
        }).forEach(entry -> {
            resourceOutput.accept(entry.getKey(), entry.getValue());
        });
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return this.paths.get(type).keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
    }
}
