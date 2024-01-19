package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.impl.config.serialization.EnumProvider;
import fuzs.puzzleslib.impl.config.serialization.RegistryProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

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
     * Create a new provider backed by a registry.
     *
     * @param enumClazz class for retrieving enum constants
     * @param <T>       the type of value
     * @return the provider
     */
    static <T extends Enum<T>> KeyedValueProvider<T> enumConstants(Class<T> enumClazz) {
        return new EnumProvider<>(enumClazz);
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
    static <T> List<String> toString(ResourceKey<? extends Registry<? super T>> registryKey, T... entries) {
        return toString(KeyedValueProvider.registryEntries(registryKey), entries);
    }

    /**
     * Converts a bunch of enum constants to their respective key as string.
     *
     * @param enumClazz class for retrieving enum constants
     * @param entries   entries to convert to string
     * @param <T>       type of value
     * @return entries as string list
     */
    @SafeVarargs
    static <T extends Enum<T>> List<String> toString(Class<T> enumClazz, T... entries) {
        return toString(KeyedValueProvider.enumConstants(enumClazz), entries);
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
    static <T> List<String> toString(KeyedValueProvider<T> valueProvider, T... entries) {
        return Stream.of(entries).peek(Objects::requireNonNull).map(valueProvider::getKey).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.toList());
    }

    /**
     * Get a value via the provider.
     *
     * @param name registered identifier
     * @return the value corresponding to the identifier
     */
    Optional<T> getValue(ResourceLocation name);

    /**
     * Get am identifier via the provider.
     *
     * @param value registered value
     * @return the identifier corresponding to the value
     */
    ResourceLocation getKey(T value);

    /**
     * Stream of all values paired with identifier available through this provider.
     *
     * @return all values including corresponding identifier
     */
    Stream<Map.Entry<ResourceLocation, T>> stream();

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
