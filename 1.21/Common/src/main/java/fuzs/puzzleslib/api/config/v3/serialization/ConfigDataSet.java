package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.impl.config.serialization.ConfigDataSetImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * Compiles a list of strings into entries of a Minecraft game registry, supports individual entries and tags, as well as pattern matching using '*'.
 *
 * @param <T> registry entry type for stored values
 */
public interface ConfigDataSet<T> extends Collection<T> {
    /**
     * Default config option comment for options backed by {@link ConfigDataSet}.
     */
    String CONFIG_DESCRIPTION = "Format for every entry is \"<namespace>:<path>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color. Begin an entry with \"!\" to make sure it is excluded, useful e.g. when it has already been matched by another pattern.";
    /**
     * Config option comment excluding notion regarding tags for options backed by {@link ConfigDataSet}.
     */
    String CONFIG_DESCRIPTION_WITHOUT_TAGS = "Format for every entry is \"<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color. Begin an entry with \"!\" to make sure it is excluded, useful e.g. when it has already been matched by another pattern.";

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link Registry} and a list of values to parse. The underlying data structure will be similar to a set.
     *
     * @param registryKey registry for type
     * @param values      values backing this set
     * @param <T>         registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, String... values) {
        return from(KeyedValueProvider.registryEntries(registryKey), values);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link Registry} and a list of values to parse, possibly including attached data types.
     * <p>If no data types are specified, the config data set will only contain simple values, the underlying data structure will be similar to a set.
     * <p>Otherwise, the config data set will contain values as keys with associated data values in the form of <code>Object[]</code>, the underlying data structure is similar to a map.
     *
     * @param registryKey registry for type
     * @param values      values backing this set
     * @param types       attached data types
     * @param <T>         registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, List<String> values, Class<?>... types) {
        return from(KeyedValueProvider.registryEntries(registryKey), values, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link Registry} and a list of values to parse, including attached data types.
     * <p>The config data set will contain values as keys with associated data values in the form of <code>Object[]</code>, the underlying data structure is similar to a map.
     *
     * @param registryKey registry for type
     * @param values      values backing this set
     * @param filter      filter for verifying data values, the filter is passed the index of the data type in <code>types</code> and then the read data value to be tested
     * @param types       attached data types
     * @param <T>         registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        return from(KeyedValueProvider.registryEntries(registryKey), values, filter, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link KeyedValueProvider} and a list of values to parse. The underlying data structure will be similar to a set.
     *
     * @param valueProvider provider for all types
     * @param values        values backing this set
     * @param <T>           registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, String... values) {
        return from(valueProvider, Arrays.asList(values));
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link KeyedValueProvider} and a list of values to parse, possibly including attached data types.
     * <p>If no data types are specified, the config data set will only contain simple values, the underlying data structure will be similar to a set.
     * <p>Otherwise, the config data set will contain values as keys with associated data values in the form of <code>Object[]</code>, the underlying data structure is similar to a map.
     *
     * @param valueProvider provider for all types
     * @param values        values backing this set
     * @param types         attached data types
     * @param <T>           registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, List<String> values, Class<?>... types) {
        return from(valueProvider, values, (index, value) -> true, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link KeyedValueProvider} and a list of values to parse, including attached data types.
     * <p>The config data set will contain values as keys with associated data values in the form of <code>Object[]</code>, the underlying data structure is similar to a map.
     *
     * @param valueProvider provider for all types
     * @param values        values backing this set
     * @param filter        filter for verifying data values, the filter is passed the index of the data type in <code>types</code> and then the read data value to be tested
     * @param types         attached data types
     * @param <T>           registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        return new ConfigDataSetImpl<>(valueProvider, values, filter, types);
    }

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
    @Nullable Object[] get(T entry);

    /**
     * Get data for an entry at an index, will throw an exception if index is out of bounds or no data is available.
     *
     * @param entry entry to query data for
     * @param index index to get data at
     * @param <V>   type of data
     * @return the data
     */
    <V> V get(T entry, int index);

    /**
     * A maximally light approach to retrieving data, will not throw an exception in any case.
     * <p>If no data is found the optional will simply be empty.
     *
     * @param entry entry to query data for
     * @param index index to get data at
     * @param <V>   type of data
     * @return the data
     */
    <V> Optional<V> getOptional(T entry, int index);

    @ApiStatus.Internal
    @Override
    boolean add(T t);

    @ApiStatus.Internal
    @Override
    boolean remove(Object o);

    @ApiStatus.Internal
    @Override
    boolean addAll(@NotNull Collection<? extends T> c);

    @ApiStatus.Internal
    @Override
    boolean removeAll(@NotNull Collection<?> c);

    @ApiStatus.Internal
    @Override
    boolean retainAll(@NotNull Collection<?> c);

    @ApiStatus.Internal
    @Override
    void clear();
}
