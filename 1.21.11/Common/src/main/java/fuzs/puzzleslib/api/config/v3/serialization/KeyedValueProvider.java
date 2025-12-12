package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.puzzleslib.impl.config.serialization.RegistryProvider;
import fuzs.puzzleslib.impl.data.SortingTagBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of certain values, usually backed by something like a {@link Registry}.
 *
 * @param <T> the type of value
 */
public interface KeyedValueProvider<T> {

    /**
     * Create a new provider backed by a registry.
     *
     * @param registryKey key for retrieving backing registry
     * @param <T>         the type of value
     * @return the provider
     */
    static <T> KeyedValueProvider<T> registryEntries(ResourceKey<? extends Registry<? super T>> registryKey) {
        return new RegistryProvider<>(registryKey);
    }

    /**
     * Creates an {@link AbstractTagAppender} instance that can be converted to a string list by calling
     * {@link AbstractTagAppender#asStringList()}.
     *
     * @param registryKey the registry to get entry keys from
     * @param <T>         the type of values
     * @return the tag appender
     */
    static <T> AbstractTagAppender<T> tagAppender(ResourceKey<? extends Registry<? super T>> registryKey) {
        return AbstractTagProvider.createTagAppender(new SortingTagBuilder(), registryKey);
    }

    /**
     * Converts a bunch of entries from a registry to their respective key as string.
     *
     * @param registryKey registry to get entry keys from
     * @param entries     entries to convert to string
     * @param <T>         type of value
     * @return entries as string list
     */
    @SafeVarargs
    static <T> List<String> asString(ResourceKey<? extends Registry<? super T>> registryKey, T... entries) {
        return asString(KeyedValueProvider.registryEntries(registryKey), entries);
    }

    /**
     * Converts a bunch of entries to their respective key as string via a {@link KeyedValueProvider}.
     *
     * @param valueProvider provider instance
     * @param entries       entries to convert to string
     * @param <T>           type of value
     * @return entries as string list
     */
    @SafeVarargs
    static <T> List<String> asString(KeyedValueProvider<T> valueProvider, T... entries) {
        return Stream.of(entries)
                .peek(Objects::requireNonNull)
                .map(valueProvider::getKey)
                .filter(Objects::nonNull)
                .map(Identifier::toString)
                .collect(Collectors.toList());
    }

    /**
     * Get a value via the provider.
     *
     * @param name registered identifier
     * @return the value corresponding to the identifier
     */
    Optional<T> getValue(Identifier name);

    /**
     * Get am identifier via the provider.
     *
     * @param value registered value
     * @return the identifier corresponding to the value
     */
    Identifier getKey(T value);

    /**
     * Stream of all values paired with identifier available through this provider.
     *
     * @return all values including corresponding identifier
     */
    Stream<Map.Entry<Identifier, T>> stream();

    /**
     * Stream of all values available through this provider.
     *
     * @return all values
     */
    Stream<T> streamValues();

    /**
     * Name of this provider, mainly used for debugging purposes.
     *
     * @return provider name
     */
    String name();
}
