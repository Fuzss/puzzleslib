package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.impl.config.serialization.ConfigDataSetImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * Compiles a list of strings into entries of a Minecraft game registry, supports individual entries and tags, as well
 * as pattern matching using <code>*</code>.
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
    String CONFIG_DESCRIPTION_WITHOUT_TAGS = "Format for every entry is \"<namespace>:<path>\". Tags are not supported. Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color. Begin an entry with \"!\" to make sure it is excluded, useful e.g. when it has already been matched by another pattern.";

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link Registry} and a list of values to parse. The
     * underlying data structure will be similar to a set.
     *
     * @param registryKey the registry
     * @param values      the values backing this set
     * @param <T>         the registry type
     * @return builder backed by a registry
     */
    static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, String... values) {
        return from(KeyedValueProvider.registryEntries(registryKey), values);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link Registry} and a list of values to parse, possibly
     * including attached data types.
     * <p>
     * If no data types are specified, the config data set will only contain simple values, the underlying data
     * structure will be similar to a set.
     * <p>
     * Otherwise, the config data set will contain values as keys with associated data values in the form of object
     * array, the underlying data structure is similar to a map.
     *
     * @param registryKey the registry
     * @param values      the values backing this set
     * @param types       the attached data types
     * @param <T>         the registry type
     * @return builder backed by a registry
     */
    static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, List<String> values, Class<?>... types) {
        return from(KeyedValueProvider.registryEntries(registryKey), values, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link Registry} and a list of values to parse,
     * including attached data types.
     * <p>
     * The config data set will contain values as keys with associated data values in the form of object array, the
     * underlying data structure is similar to a map.
     *
     * @param registryKey the registry
     * @param values      the values backing this set
     * @param filter      the filter for verifying data values, which is passed the index of the data type in the types
     *                    array, in addition to the read data value to be tested
     * @param types       the attached data types
     * @param <T>         the registry type
     * @return builder backed by a registry
     */
    static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        return from(KeyedValueProvider.registryEntries(registryKey), values, filter, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link KeyedValueProvider} and a list of values to
     * parse. The underlying data structure will be similar to a set.
     *
     * @param valueProvider the provider for all types
     * @param values        the values backing this set
     * @param <T>           the value type
     * @return builder backed by provided values
     */
    static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, String... values) {
        return from(valueProvider, Arrays.asList(values));
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link KeyedValueProvider} and a list of values to
     * parse, possibly including attached data types.
     * <p>
     * If no data types are specified, the config data set will only contain simple values, the underlying data
     * structure will be similar to a set.
     * <p>
     * Otherwise, the config data set will contain values as keys with associated data values in the form of object
     * array, the underlying data structure is similar to a map.
     *
     * @param valueProvider the provider for all types
     * @param values        the values backing this set
     * @param types         the attached data types
     * @param <T>           the value type
     * @return builder backed by provided values
     */
    static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, List<String> values, Class<?>... types) {
        return from(valueProvider, values, (index, value) -> true, types);
    }

    /**
     * Creates a new {@link ConfigDataSet} instance for a given {@link KeyedValueProvider} and a list of values to
     * parse, including attached data types.
     * <p>
     * The config data set will contain values as keys with associated data values in the form of object array, the
     * underlying data structure is similar to a map.
     *
     * @param valueProvider the provider for all types
     * @param values        the values backing this set
     * @param filter        the filter for verifying data values, which is passed the index of the data type in the
     *                      types array, in addition to the read data value to be tested
     * @param types         the attached data types
     * @param <T>           the value type
     * @return builder backed by provided values
     */
    static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        return new ConfigDataSetImpl<>(valueProvider, values, filter, types);
    }

    /**
     * @return the dissolved entry map backing this config data set (values are mostly an empty array, so this is more
     *         like a set actually)
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
}
