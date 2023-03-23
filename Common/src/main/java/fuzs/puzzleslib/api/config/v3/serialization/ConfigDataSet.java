package fuzs.puzzleslib.api.config.v3.serialization;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * compiles a list of strings into entries of a Minecraft game registry, supports individual entries and tags,
 * as well as pattern matching using a '*' char somewhere in the path part of the provided {@link ResourceLocation}
 *
 * @param <T> registry entry type for stored values
 */
public interface ConfigDataSet<T> extends Collection<T> {
    /**
     * default config option comment for options backed by {@link ConfigDataSet}
     */
    String CONFIG_DESCRIPTION = "Format for every entry is \"<namespace>:<path>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. Path may use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*shulker_box\" to match all shulker boxes no matter of color.";

    /**
     * @return the dissolved entry map backing this config data set (values are mostly an empty array, so this is more like a set actually)
     */
    Map<T, Object[]> toMap();

    /**
     * @return the dissolved entry set backing this config data set
     */
    default Set<T> toSet() {
        return this.toMap().keySet();
    }

    @Override
    default Iterator<T> iterator() {
        return this.toSet().iterator();
    }

    @Override
    default int size() {
        return this.toMap().size();
    }

    @Override
    default boolean isEmpty() {
        return this.toMap().isEmpty();
    }

    @Override
    default boolean contains(Object o) {
        return this.toSet().contains(o);
    }

    @NotNull
    @Override
    default Object[] toArray() {
        return this.toSet().toArray();
    }

    @NotNull
    @Override
    default <T1> T1[] toArray(@NotNull T1[] a) {
        return this.toSet().toArray(a);
    }

    @Deprecated
    @Override
    default boolean add(T t) {
        return this.toSet().add(t);
    }

    @Deprecated
    @Override
    default boolean remove(Object o) {
        return this.toSet().remove(o);
    }

    @Override
    default boolean containsAll(@NotNull Collection<?> c) {
        return this.toSet().containsAll(c);
    }

    @Deprecated
    @Override
    default boolean addAll(@NotNull Collection<? extends T> c) {
        return this.toSet().addAll(c);
    }

    @Deprecated
    @Override
    default boolean removeAll(@NotNull Collection<?> c) {
        return this.toSet().removeAll(c);
    }

    @Deprecated
    @Override
    default boolean retainAll(@NotNull Collection<?> c) {
        return this.toSet().retainAll(c);
    }

    @Deprecated
    @Override
    default void clear() {
        this.toMap().clear();
    }

    /**
     * Queries data for a given value from this set.
     *
     * @param entry entry to query data for
     * @return data array
     *
     * @throws NullPointerException if <code>entry</code> is not present for this set
     */
    default Object[] get(T entry) {
        if (!this.contains(entry)) throw new NullPointerException("no data found for %s".formatted(entry));
        return this.toMap().get(entry);
    }

    /**
     * @param registryKey registry for type
     * @param values values backing this set
     * @param <T> registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(final ResourceKey<? extends Registry<T>> registryKey, List<String> values) {
        return from(registryKey, values, (index, value) -> true);
    }

    /**
     * @param registryKey registry for type
     * @param values values backing this set
     * @param filter filter for verifying values and attached data
     * @param types attached data types
     * @param <T> registry type
     * @return builder backed by <code>registry</code>
     */
    static <T> ConfigDataSet<T> from(final ResourceKey<? extends Registry<T>> registryKey, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        return new ConfigDataSetImpl<>(getRegistryFromKey(registryKey), values, filter, types);
    }

    /**
     * converts a bunch of entries from a registry to their respective key as string
     *
     * @param registryKey registry to get entry keys from
     * @param entries entries to convert to string
     * @param <T> registry element type
     * @return entries as string list
     */
    @SafeVarargs
    static <T> List<String> toString(final ResourceKey<? extends Registry<T>> registryKey, T... entries) {
        Registry<? super T> registry = getRegistryFromKey(registryKey);
        return Stream.of(entries)
                .peek(Objects::requireNonNull)
                .map(registry::getKey)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * @param registryKey the key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    @SuppressWarnings("unchecked")
    private static <T> Registry<T> getRegistryFromKey(ResourceKey<? extends Registry<T>> registryKey) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
        Objects.requireNonNull(registry, String.format("Registry for key %s not found", registryKey));
        return registry;
    }
}
