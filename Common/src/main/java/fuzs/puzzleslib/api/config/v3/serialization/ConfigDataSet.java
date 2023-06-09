package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.impl.config.serialization.ConfigDataSetImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compiles a list of strings into entries of a Minecraft game registry, supports individual entries and tags, as well as pattern matching using '*'.
 *
 * @param <T> registry entry type for stored values
 */
public interface ConfigDataSet<T> extends Collection<T> {
    /**
     * default config option comment for options backed by {@link ConfigDataSet}
     */
    String CONFIG_DESCRIPTION = "Format for every entry is \"<namespace>:<path>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color.";

    /**
     * @return the dissolved entry map backing this config data set (values are mostly an empty array, so this is more like a set actually)
     */
    Map<T, Object[]> toMap();

    /**
     * @return the dissolved entry set backing this config data set
     */
    Set<T> toSet();

    /**
     * Queries data for a given value from this config data set.
     *
     * @param entry entry to query data for
     * @return data array or <code>null</code>
     */
    @Nullable
    Object[] get(T entry);

    /**
     * Get data for an entry at an index, will throw an exception if index is out of bounds or no data is available.
     *
     * @param entry entry to query data for
     * @param index index to get data at
     * @param <V> type of data
     * @return the data
     */
    <V> V get(T entry, int index);

    /**
     * A maximally light approach to retrieving data, will not throw an exception in any case.
     * <p>If no data is found the optional will simply be empty.
     *
     * @param entry entry to query data for
     * @param index index to get data at
     * @param <V> type of data
     * @return the data
     */
    <V> Optional<V> getOptional(T entry, int index);

    @Deprecated
    @Override
    boolean add(T t);

    @Deprecated
    @Override
    boolean remove(Object o);

    @Deprecated
    @Override
    boolean addAll(@NotNull Collection<? extends T> c);

    @Deprecated
    @Override
    boolean removeAll(@NotNull Collection<?> c);

    @Deprecated
    @Override
    boolean retainAll(@NotNull Collection<?> c);

    @Deprecated
    @Override
    void clear();

    /**
     * Creates a new {@link ConfigDataSet} instance for a given registry and a list of values to parse, possibly including attached data types.
     * <p>If no data types are specified, the config data set will only contain simple values, the underlying data structure will be similar to a set.
     * <p>Otherwise, the config data set will contain values as keys with associated data values in the form of <code>Object[]</code>, the underlying data structure is similar to a map.
     *
     * @param registryKey registry for type
     * @param values      values backing this set
     * @param types       attached data types
     * @param <T>         registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(final ResourceKey<? extends Registry<T>> registryKey, List<String> values, Class<?>... types) {
        return from(registryKey, values, (index, value) -> true, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given registry and a list of values to parse, including attached data types.
     * <p>The config data set will contain values as keys with associated data values in the form of <code>Object[]</code>, the underlying data structure is similar to a map.
     *
     * @param registryKey registry for type
     * @param values      values backing this set
     * @param filter      filter for verifying data values, the filter is passed the index of the data type in <code>types</code> and then the read data value to be tested
     * @param types       attached data types
     * @param <T>         registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(final ResourceKey<? extends Registry<T>> registryKey, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        return new ConfigDataSetImpl<>(findRegistry(registryKey, false), values, filter, types);
    }

    /**
     * Converts a bunch of entries from a registry to their respective key as string.
     *
     * @param registryKey registry to get entry keys from
     * @param entries     entries to convert to string
     * @param <T>         registry element type
     * @return entries as string list
     */
    @SafeVarargs
    static <T> List<String> toString(final ResourceKey<? extends Registry<T>> registryKey, T... entries) {
        Registry<? super T> registry = findRegistry(registryKey, false);
        return Stream.of(entries)
                .peek(Objects::requireNonNull)
                .map(registry::getKey)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * Finds a built-in registry from the provided {@link ResourceKey}, dynamic registries are only supported when a game server is already running.
     * <p>This cannot be used in configs unfortunately, as even for server configs no valid registry access has been created yet when the config is initialized.
     *
     * @param registryKey the key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    @SuppressWarnings("unchecked")
    static <T> Registry<T> findRegistry(ResourceKey<? extends Registry<T>> registryKey, boolean dynamicRegistries) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
        if (registry != null) return registry;
        if (dynamicRegistries && Proxy.INSTANCE.getGameServer() != null) {
            registry = Proxy.INSTANCE.getGameServer().registryAccess().registry(registryKey).orElse(null);
            if (registry != null) return registry;
        }
        throw new IllegalArgumentException("Registry for key %s not found".formatted(registryKey));
    }
}
