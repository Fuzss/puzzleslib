package fuzs.puzzleslib.config.serialization;

import com.google.common.collect.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
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
    private static final Set<Class<?>> SUPPORTED_DATA_TYPES = ImmutableSet.of(Boolean.class, Integer.class, Double.class, String.class);

    /**
     * registry to work with
     */
    private final Registry<T> activeRegistry;
    /**
     * internal entry holder storage, will be dissolved when {@link #toMap()} is called for the first time
     */
    private final List<EntryHolder<?, T>> values = Lists.newArrayList();
    /**
     * filter for when {@link EntryHolder}s are constructed, first argument is index (only index 0 when no data is specified), second is entry/data value
     */
    private final BiPredicate<Integer, Object> filter;
    /**
     * dissolved {@link #values}
     */
    private Map<T, Object[]> dissolved;

    /**
     * @param registry      registry entries the to be created collections contain
     * @param values        string values to build this set from
     * @param filter        filter for verifying <code>values</code>
     * @param types         additional data types, comma-separated as part of <code>values</code>
     */
    ConfigDataSetImpl(Registry<T> registry, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
        this.activeRegistry = registry;
        this.filter = filter;
        for (Class<?> clazz : types) {
            if (!SUPPORTED_DATA_TYPES.contains(clazz)) throw new IllegalArgumentException("data type of clazz %s is not supported".formatted(clazz));
        }
        for (String value : values) {
            this.deserialize(value, types).ifPresent(this.values::add);
        }
    }

    @Override
    public Map<T, Object[]> toMap() {
        this.dissolve();
        return this.dissolved;
    }

    /**
     * dissolve {@link #values}, always call before retrieving any data from this set
     */
    private void dissolve() {
        if (this.dissolved == null) {
            ImmutableMap.Builder<T, Object[]> builder = ImmutableMap.builder();
            // split this to ensure data values from individual entries take precedence over tag entries
            for (EntryHolder<?, T> holder : this.values) {
                if (holder instanceof ConfigDataSetImpl.TagEntryHolder<?>) {
                    this.dissolveHolder(holder, builder::putAll);
                }
            }
            for (EntryHolder<?, T> holder : this.values) {
                if (holder instanceof ConfigDataSetImpl.RegistryEntryHolder<?>) {
                    this.dissolveHolder(holder, builder::putAll);
                }
            }
            this.dissolved = builder.build();
        }
    }

    /**
     * collects all values from a {@link EntryHolder}, checks if they are valid using {@link #filter}, then adds them to the map builder
     *
     * @param holder dissolve a single {@link EntryHolder}
     * @param builder immutable map builder to add values to
     */
    private void dissolveHolder(EntryHolder<?, T> holder, Consumer<Map<T, Object[]>> builder) {
        Map<T, Object[]> entries = Maps.newHashMap();
        holder.dissolve(entries);
        entries.keySet().removeIf(e -> !this.filter.test(0, e));
        builder.accept(entries);
    }

    /**
     * @param source string source
     * @param types additional data values
     * @return entry holder from <code>source</code>, empty if there was an exception during deserialization
     */
    private Optional<EntryHolder<?, T>> deserialize(String source, Class<?>[] types) {
        String[] sources = source.trim().split(",");
        Object[] data = new Object[types.length];
        try {
            for (int i = 0; i < types.length; i++) {
                if (sources.length - 1 <= i) {
                    throw new IllegalArgumentException("data index out of bounds, index was %s, but length is %s".formatted(i + 1, sources.length));
                }
                data[i] = deserializeData(types[i], sources[i + 1].trim());
                if (!this.filter.test(i + 1, data[i])) {
                    throw new IllegalStateException("data %s at index %s from source entry %s does not satisfy filter".formatted(data[i], i, source));
                }
            }
            return Optional.of(this.deserialize(sources[0].trim()).withData(data));
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
        String[] splitSource = source.split(":");
        return switch (splitSource.length) {
            case 1 -> {
                // tag # must be at namespace, not path
                if (splitSource[0].startsWith("#")) {
                    yield this.makeHolder("#minecraft", splitSource[0].substring(1));
                }
                yield this.makeHolder("minecraft", splitSource[0]);
            }
            case 2 -> this.makeHolder(splitSource[0], splitSource[1]);
            default -> throw new IllegalArgumentException("%s is no valid resource location format".formatted(source));
        };
    }

    /**
     * @param namespace namespace part of {@link ResourceLocation}
     * @param path path part of {@link ResourceLocation}
     * @return entry holder from <code>namespace</code> and <code>path</code>
     *
     * @throws RuntimeException if the format is no valid {@link ResourceLocation}
     */
    private EntryHolder<?, T> makeHolder(String namespace, String path) throws RuntimeException {
        if (namespace.isEmpty() || path.isEmpty()) {
            throw new IllegalArgumentException("%s:%s is no valid resource location format".formatted(namespace, path));
        }
        if (namespace.startsWith("#")) {
            return new TagEntryHolder<>(this.activeRegistry, namespace.substring(1), path);
        } else {
            return new RegistryEntryHolder<>(this.activeRegistry, namespace, path);
        }
    }

    /**
     * @param clazz clazz type of parameter
     * @param source type as string
     * @return <code>source</code> converted to type <code>clazz</code>
     *
     * @throws RuntimeException if <code>clazz</code> is not supported
     */
    private static Object deserializeData(Class<?> clazz, String source) throws RuntimeException {
        if (clazz == Boolean.class) {
            if (source.equals("true")) return true;
            if (source.equals("false")) return false;
            throw new IllegalArgumentException("%s is not a boolean value".formatted(source));
        } else if (clazz == Integer.class) {
            return Integer.parseInt(source);
        } else if (clazz == Double.class) {
            return Double.parseDouble(source);
        } else if (clazz == String.class) {
            return source;
        }
        throw new IllegalArgumentException("data type of clazz %s is not supported".formatted(clazz));
    }

    /**
     * holds a single entry from a string list, ready to be dissolved into registry entries when required
     * <p>supports pattern matching for {@link ResourceLocation} path, therefore namespace and path are stored separately instead of as {@link ResourceLocation}
     *
     * @param <D> raw value type, single registry entry or tag
     * @param <E> value type for set, result from dissolving
     */
    private static abstract class EntryHolder<D, E> {
        /**
         * the registry to compile values from
         */
        protected final Registry<E> activeRegistry;
        /**
         * namespace part of {@link ResourceLocation}
         */
        private final String namespace;
        /**
         * path part of {@link ResourceLocation}
         */
        private final String path;
        /**
         * data to add to every single value constructed from this entry when dissolving in {@link #dissolve()}
         * <p>set a new value via {@link #withData}
         */
        private Object[] data = new Object[0];

        /**
         * @param registry the registry to compile values from
         * @param namespace namespace part of {@link ResourceLocation}
         * @param path path part of {@link ResourceLocation}
         */
        protected EntryHolder(Registry<E> registry, String namespace, String path) {
            this.activeRegistry = registry;
            this.namespace = namespace;
            this.path = path;
        }

        /**
         * add additional data to this holder
         *
         * @param data      data to add
         * @return          this usable as builder
         */
        public EntryHolder<D, E> withData(Object[] data) {
            this.data = data;
            return this;
        }

        /**
         * compile all entries from this holder
         *
         * @param entries       provided entries to add to
         */
        public final void dissolve(Map<E, Object[]> entries) {
            this.findRegistryMatches(this.namespace, this.path).stream().flatMap(this::dissolveValue).forEach(value -> entries.put(value, this.data));
        }

        /**
         * retrieve all matching entries from registry, handles pattern matching
         *
         * @param namespace         {@link ResourceLocation} namespace
         * @param path              {@link ResourceLocation} path
         * @return                  all entries in registry matching <code>namespace</code> and <code>path</code>
         */
        private Collection<D> findRegistryMatches(String namespace, String path) {
            Collection<D> matches = Sets.newHashSet();
            if (!path.contains("*")) {
                this.toValue(new ResourceLocation(namespace, path)).ifPresent(matches::add);
            } else {
                String regexPath = path.replace("*", "[a-z0-9/._-]*");
                this.allValues()
                        .filter(entry -> entry.getKey().getNamespace().equals(namespace))
                        .filter(entry -> entry.getKey().getPath().matches(regexPath))
                        .map(Map.Entry::getValue)
                        .forEach(matches::add);
            }
            // test if this is a valid entry first
            if (this.activeRegistry != null && matches.isEmpty()) {
                PuzzlesLib.LOGGER.warn("Unable to parse entry {}:{}: No matches found in registry {}", namespace, path, this.activeRegistry.key().location());
            }
            return matches;
        }

        /**
         * @param entry         raw entry
         * @return              dissolved <code>entry</code> for flat mapping
         */
        protected abstract Stream<E> dissolveValue(D entry);

        /**
         * @param identifier        the key associated with a value
         * @return                  value from registry for <code>identifier</code>
         */
        protected abstract Optional<D> toValue(ResourceLocation identifier);

        /**
         * @return              all registry values for pattern matching
         */
        protected abstract Stream<Map.Entry<ResourceLocation, D>> allValues();
    }

    /**
     * implementation for a single registry value
     *
     * @param <E> registry type
     */
    private static class RegistryEntryHolder<E> extends EntryHolder<E, E> {

        /**
         * @param registry the registry to compile values from
         * @param namespace namespace part of {@link ResourceLocation}
         * @param path path part of {@link ResourceLocation}
         */
        RegistryEntryHolder(Registry<E> registry, String namespace, String path) {
            super(registry, namespace, path);
        }

        @Override
        protected Stream<E> dissolveValue(E entry) {
            return Stream.of(entry);
        }

        @Override
        protected Optional<E> toValue(ResourceLocation identifier) {
            if (!this.activeRegistry.containsKey(identifier)) return Optional.empty();
            return Optional.ofNullable(this.activeRegistry.get(identifier));
        }

        @Override
        protected Stream<Map.Entry<ResourceLocation, E>> allValues() {
            return this.activeRegistry.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().location(), Map.Entry::getValue)).entrySet().stream();
        }
    }

    /**
     * implementation for a registry tag value
     *
     * @param <E> registry type
     */
    private static class TagEntryHolder<E> extends EntryHolder<TagKey<E>, E> {

        /**
         * @param registry the registry to compile values from
         * @param namespace namespace part of {@link ResourceLocation}
         * @param path path part of {@link ResourceLocation}
         */
        TagEntryHolder(Registry<E> registry, String namespace, String path) {
            super(registry, namespace, path);
        }

        @Override
        public Stream<E> dissolveValue(TagKey<E> entry) {
            return StreamSupport.stream(this.activeRegistry.getTagOrEmpty(entry).spliterator(), false).map(Holder::value);
        }

        @Override
        protected Optional<TagKey<E>> toValue(ResourceLocation identifier) {
            TagKey<E> tag = TagKey.create(this.activeRegistry.key(), identifier);
            if (!this.activeRegistry.isKnownTagName(tag)) return Optional.empty();
            return Optional.of(tag);
        }

        @Override
        protected Stream<Map.Entry<ResourceLocation, TagKey<E>>> allValues() {
            return this.activeRegistry.getTagNames().collect(Collectors.toMap(TagKey::location, Function.identity())).entrySet().stream();
        }
    }
}