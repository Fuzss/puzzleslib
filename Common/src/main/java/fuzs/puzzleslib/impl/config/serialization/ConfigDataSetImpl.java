package fuzs.puzzleslib.impl.config.serialization;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * implementation of {@link ConfigDataSet}
 *
 * @param <T> registry entry type for stored values
 */
public final class ConfigDataSetImpl<T> implements ConfigDataSet<T> {
    /**
     * the data types we currently are able to handle
     */
    private static final Set<Class<?>> SUPPORTED_DATA_TYPES = ImmutableSet.of(boolean.class,
            Boolean.class,
            int.class,
            Integer.class,
            double.class,
            Double.class,
            String.class);

    /**
     * registry to work with
     */
    private final KeyedValueProvider<T> valueProvider;
    /**
     * internal entry holder storage, will be dissolved when {@link #toMap()} is called for the first time
     */
    private final List<EntryHolder<?, T>> values = new ArrayList<>();
    /**
     * filter for when {@link EntryHolder}s are constructed, first argument is index (only index 0 when no data is
     * specified), second is entry/data value
     */
    private final BiPredicate<Integer, Object> filter;
    private final int dataSize;
    /**
     * dissolved {@link #values}
     */
    private Map<T, Object[]> dissolved;

    /**
     * @param valueProvider registry entries the to be created collections contain
     * @param values        string values to build this set from
     * @param filter        filter for verifying <code>values</code>
     * @param types         additional data types, comma-separated as part of <code>values</code>
     */
    public ConfigDataSetImpl(KeyedValueProvider<T> valueProvider, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        this.valueProvider = valueProvider;
        this.filter = filter;
        for (Class<?> clazz : types) {
            if (!SUPPORTED_DATA_TYPES.contains(clazz)) {
                throw new IllegalArgumentException("Data type of clazz %s is not supported".formatted(clazz));
            }
        }

        this.dataSize = types.length;
        for (String value : values) {
            this.deserialize(value, types).ifPresent(this.values::add);
        }

        TagsUpdatedCallback.EVENT.register((registryAccess, client) -> {
            this.dissolved = null;
        });
    }

    /**
     * @param clazz  clazz type of parameter
     * @param source type as string
     * @return <code>source</code> converted to type <code>clazz</code>
     *
     * @throws RuntimeException if <code>clazz</code> is not supported
     */
    private static Object deserializeData(Class<?> clazz, String source) throws RuntimeException {
        if (clazz == boolean.class || clazz == Boolean.class) {
            if (source.equals("true")) return true;
            if (source.equals("false")) return false;
            throw new IllegalArgumentException("%s is not a boolean value".formatted(source));
        } else if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(source);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(source);
        } else if (clazz == String.class) {
            return source;
        } else {
            throw new IllegalArgumentException("Data type of clazz %s is not supported".formatted(clazz));
        }
    }

    @Override
    public Map<T, Object[]> toMap() {
        return this.dissolve();
    }

    @Override
    public Set<T> toSet() {
        return this.toMap().keySet();
    }

    @Override
    public Iterator<T> iterator() {
        return this.toSet().iterator();
    }

    @Override
    public int size() {
        return this.toMap().size();
    }

    @Override
    public boolean isEmpty() {
        return this.toMap().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.toSet().contains(o);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.toSet().toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return this.toSet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.toSet().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.toSet().remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.toSet().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return this.toSet().addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.toSet().removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.toSet().retainAll(c);
    }

    @Override
    public void clear() {
        this.toMap().clear();
    }

    @Nullable
    @Override
    public Object[] get(T entry) {
        return this.toMap().get(entry);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(T entry, int index) {
        Objects.checkIndex(index, this.dataSize);
        Object[] data = this.get(entry);
        Objects.requireNonNull(data, "data is null");
        return (V) data[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> getOptional(T entry, int index) {
        if (index < 0 || index >= this.dataSize) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(this.get(entry)).map(data -> (V) data[index]);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConfigDataSet<?> impl && this.toMap().equals(impl.toMap());
    }

    @Override
    public int hashCode() {
        return this.toMap().hashCode();
    }

    private Map<T, Object[]> dissolve() {
        Map<T, Object[]> dissolved = this.dissolved;
        if (dissolved == null) {
            Map<T, Object[]> entries = new IdentityHashMap<>();
            Set<T> toRemove = Sets.newIdentityHashSet();
            // split this to ensure data values from individual entries take precedence over tag entries
            for (EntryHolder<?, T> holder : this.values) {
                if (holder instanceof ConfigDataSetImpl.TagEntryHolder<?>) {
                    holder.dissolve(holder.inverted ? (t, objects) -> toRemove.add(t) : entries::put);
                }
            }

            for (EntryHolder<?, T> holder : this.values) {
                if (holder instanceof ConfigDataSetImpl.RegistryEntryHolder<?>) {
                    holder.dissolve(holder.inverted ? (t, objects) -> toRemove.add(t) : entries::put);
                }
            }

            if (entries.isEmpty() && !toRemove.isEmpty()) {
                entries = this.valueProvider.streamValues()
                        .collect(Collectors.toMap(Function.identity(),
                                (T t) -> EntryHolder.EMPTY_DATA,
                                (Object[] o1, Object[] o2) -> o2,
                                IdentityHashMap::new));
            }

            entries.keySet().removeIf(t -> !this.filter.test(0, t) || toRemove.contains(t));
            return this.dissolved = Collections.unmodifiableMap(entries);
        } else {
            return dissolved;
        }
    }

    /**
     * @param source string source
     * @param types  additional data values
     * @return entry holder from <code>source</code>, empty if there was an exception during deserialization
     */
    private Optional<EntryHolder<?, T>> deserialize(String source, Class<?>[] types) {
        String[] sources = source.trim().split(",");
        try {
            String newSource = sources[0].trim();
            if (!newSource.startsWith("!")) {
                Object[] data = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    if (sources.length - 1 <= i) {
                        throw new IllegalArgumentException("Data index out of bounds, index was %s, but length is %s".formatted(
                                i + 1,
                                sources.length));
                    }
                    data[i] = deserializeData(types[i], sources[i + 1].trim());
                    if (!this.filter.test(i + 1, data[i])) {
                        throw new IllegalStateException(
                                "Data %s at index %s from source entry %s does not conform to filter".formatted(data[i],
                                        i,
                                        source));
                    }
                }

                return Optional.of(this.deserialize(newSource).withData(data));
            } else {
                return Optional.of(this.deserialize(newSource));
            }
        } catch (Exception e) {
            PuzzlesLib.LOGGER.warn("Unable to parse entry {}", source, e);
            return Optional.empty();
        }
    }

    /**
     * @param source entry string source
     * @return entry holder from <code>source</code>
     *
     * @throws RuntimeException if the format is no valid {@link ResourceLocation}
     */
    private EntryHolder<?, T> deserialize(String source) throws RuntimeException {
        boolean inverted = source.startsWith("!");
        if (inverted) {
            source = source.substring(1);
        }

        boolean tagHolder = source.startsWith("#");
        if (tagHolder) {
            source = source.substring(1);
        }

        // this is necessary when applying regex matching later on, since existing resource locations are converted to string, and they will contain "minecraft"
        if (!source.contains(":")) {
            source = "minecraft:" + source;
        }

        if (tagHolder) {
            if (this.valueProvider instanceof RegistryProvider<T> registryProvider) {
                return new TagEntryHolder<>(registryProvider, source, inverted);
            } else {
                throw new IllegalArgumentException("Value provider %s does not support tags!".formatted(this.valueProvider.name()));
            }
        } else {
            return new RegistryEntryHolder<>(this.valueProvider, source, inverted);
        }
    }

    /**
     * holds a single entry from a string list, ready to be dissolved into registry entries when required
     * <p>supports pattern matching for {@link ResourceLocation} path, therefore input and path are stored separately
     * instead of as {@link ResourceLocation}
     *
     * @param <D> raw value type, single registry entry or tag
     * @param <E> value type for set, result from dissolving
     */
    private static abstract class EntryHolder<D, E> {
        public static final Object[] EMPTY_DATA = new Object[0];

        private final String providerName;
        /**
         * Is this holder meant to exclude entries from being added to the set.
         */
        public final boolean inverted;
        /**
         * the raw {@link ResourceLocation} to parse
         */
        private final String input;
        /**
         * data to add to every single value constructed from this entry when dissolving in {@link #dissolve()}
         * <p>set a new value via {@link #withData}
         */
        private Object[] data = EMPTY_DATA;

        /**
         * @param input    input part of {@link ResourceLocation}
         * @param inverted is this holder meant to exclude entries from being added to the set
         */
        protected EntryHolder(String providerName, String input, boolean inverted) {
            this.providerName = providerName;
            this.input = input;
            this.inverted = inverted;
        }

        /**
         * add additional data to this holder
         *
         * @param data data to add
         * @return this usable as builder
         */
        public EntryHolder<D, E> withData(Object[] data) {
            this.data = data;
            return this;
        }

        /**
         * compile all entries from this holder
         */
        public final void dissolve(BiConsumer<E, Object[]> builder) {
            this.findRegistryMatches(this.input)
                    .stream()
                    .flatMap(this::dissolveValue)
                    .forEach(value -> builder.accept(value, this.data));
        }

        private Collection<D> findRegistryMatches(String s) {
            Collection<D> matches = new HashSet<>();
            if (!s.contains("*")) {
                Optional.ofNullable(ResourceLocationHelper.tryParse(s)).flatMap(this::toValue).ifPresent(matches::add);
            } else {
                String regexSource = s.replace("*", "[a-z0-9/._-]*");
                this.allValues()
                        .filter(entry -> entry.getKey().toString().matches(regexSource))
                        .map(Map.Entry::getValue)
                        .forEach(matches::add);
            }

            // test if this is a valid entry first
            if (matches.isEmpty()) {
                PuzzlesLib.LOGGER.warn("Unable to parse entry {}: No matches found in {}", s, this.providerName);
            }

            return matches;
        }

        /**
         * @param entry raw entry
         * @return dissolved <code>entry</code> for flat mapping
         */
        protected abstract Stream<E> dissolveValue(D entry);

        /**
         * @param resourceLocation the key associated with a value
         * @return value from registry for the provided resource location
         */
        protected abstract Optional<D> toValue(ResourceLocation resourceLocation);

        /**
         * @return all registry values for pattern matching
         */
        protected abstract Stream<Map.Entry<ResourceLocation, D>> allValues();
    }

    /**
     * implementation for a single registry value
     *
     * @param <T> registry type
     */
    private static class RegistryEntryHolder<T> extends EntryHolder<T, T> {
        /**
         * the registry to compile values from
         */
        private final KeyedValueProvider<T> valueProvider;

        /**
         * @param valueProvider the registry to compile values from
         * @param source        the raw {@link ResourceLocation} to parse
         * @param inverted      is this holder meant to exclude entries from being added to the set
         */
        RegistryEntryHolder(KeyedValueProvider<T> valueProvider, String source, boolean inverted) {
            super(valueProvider.name(), source, inverted);
            this.valueProvider = valueProvider;
        }

        @Override
        protected Stream<T> dissolveValue(T entry) {
            return Stream.of(entry);
        }

        @Override
        protected Optional<T> toValue(ResourceLocation resourceLocation) {
            return this.valueProvider.getValue(resourceLocation);
        }

        @Override
        protected Stream<Map.Entry<ResourceLocation, T>> allValues() {
            return this.valueProvider.stream();
        }
    }

    /**
     * implementation for a registry tag value
     *
     * @param <T> registry type
     */
    private static class TagEntryHolder<T> extends EntryHolder<TagKey<T>, T> {
        /**
         * the registry to compile values from
         */
        private final Registry<T> registry;

        /**
         * @param registryProvider the registry to compile values from
         * @param source           the raw {@link ResourceLocation} to parse
         * @param inverted         is this holder meant to exclude entries from being added to the set
         */
        TagEntryHolder(RegistryProvider<T> registryProvider, String source, boolean inverted) {
            super(registryProvider.name(), source, inverted);
            this.registry = registryProvider.registry();
        }

        @Override
        public Stream<T> dissolveValue(TagKey<T> entry) {
            return StreamSupport.stream(this.registry.getTagOrEmpty(entry).spliterator(), false).map(Holder::value);
        }

        @Override
        protected Optional<TagKey<T>> toValue(ResourceLocation resourceLocation) {
            TagKey<T> tag = TagKey.create(this.registry.key(), resourceLocation);
            if (this.registry.get(tag).isEmpty()) return Optional.empty();
            return Optional.of(tag);
        }

        @Override
        protected Stream<Map.Entry<ResourceLocation, TagKey<T>>> allValues() {
            return this.registry.listTagIds().map(tagKey -> Map.entry(tagKey.location(), tagKey));
        }
    }
}