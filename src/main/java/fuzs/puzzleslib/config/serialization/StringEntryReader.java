package fuzs.puzzleslib.config.serialization;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.PuzzlesLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * parser logic for collection builder
 * @param <T> content type of collection to build
 */
public class StringEntryReader<T extends IForgeRegistryEntry<T>> {
    /**
     * registry to work with
     */
    private final IForgeRegistry<T> activeRegistry;

    /**
     * @param registry registry entries the to be created collections contain
     */
    protected StringEntryReader(IForgeRegistry<T> registry) {
        this.activeRegistry = registry;
    }

    /**
     * takes a string and finds all matching resource locations, can be multiples since wildcards are supported
     * @param source string to generate resource location from
     * @return list of matches
     */
    protected final List<T> getEntriesFromRegistry(String source) {
        return getEntriesFromRegistry(source, this.activeRegistry);
    }

    /**
     * takes a string and finds all matching resource locations, can be multiples since wildcards are supported
     * @param source string to generate resource location from
     * @param activeRegistry registry to work with
     * @return list of matches
     * @param <R> content type of registry
     */
    public static <R extends IForgeRegistryEntry<R>> List<R> getEntriesFromRegistry(String source, IForgeRegistry<R> activeRegistry) {
        List<R> foundEntries = Lists.newArrayList();
        if (source.contains("*")) {
            // an asterisk is present, so attempt to find entries including a wildcard
            foundEntries.addAll(getWildcardEntries(source, activeRegistry));
        } else {
            Optional<ResourceLocation> location = Optional.ofNullable(ResourceLocation.tryParse(source));
            if (location.isPresent()) {
                // when it's present there can't be a wildcard parameter
                Optional<R> entry = getEntryFromRegistry(location.get(), activeRegistry);
                entry.ifPresent(foundEntries::add);
            } else {
                log(source, "Entry not found");
            }
        }
        return foundEntries;
    }

    /**
     * finds the location in the active registry, otherwise the optional is empty
     * @param location location to get entry for
     * @param activeRegistry registry to work with
     * @return optional entry if found
     * @param <R> content type of registry
     */
    private static <R extends IForgeRegistryEntry<R>> Optional<R> getEntryFromRegistry(ResourceLocation location, IForgeRegistry<R> activeRegistry) {
        if (activeRegistry.containsKey(location)) {
            return Optional.ofNullable(activeRegistry.getValue(location));
        } else {
            log(location.toString(), "Entry not found");
        }
        return Optional.empty();
    }

    /**
     * split string into namespace and key to be further processed
     * @param source string to get entries for
     * @param activeRegistry registry to work with
     * @return all the entries found
     * @param <R> content type of registry
     */
    private static <R extends IForgeRegistryEntry<R>> List<R> getWildcardEntries(String source, IForgeRegistry<R> activeRegistry) {
        String[] splitSource = source.split(":");
        switch (splitSource.length) {
            case 1:
                // no colon found, so this must be an entry from Minecraft
                return getListFromRegistry("minecraft", splitSource[0], activeRegistry);
            case 2:
                return getListFromRegistry(splitSource[0], splitSource[1], activeRegistry);
            default:
                log(source, "Invalid resource location format");
                return Lists.newArrayList();
        }
    }

    /**
     * create list with entries from given namespace matching given wildcard path
     * @param namespace namespace to check
     * @param path path string including wildcard
     * @param activeRegistry registry to work with
     * @return all entries found
     * @param <R> content type of registry
     */
    private static <R extends IForgeRegistryEntry<R>> List<R> getListFromRegistry(String namespace, String path, IForgeRegistry<R> activeRegistry) {
        String regexPath = path.replace("*", "[a-z0-9/._-]*");
        List<R> entries = activeRegistry.getEntries().stream()
                .filter(entry -> entry.getKey().getRegistryName().getNamespace().equals(namespace))
                .filter(entry -> entry.getKey().getRegistryName().getPath().matches(regexPath))
                .map(Map.Entry::getValue).collect(Collectors.toList());
        if (entries.isEmpty()) {
            log(new ResourceLocation(namespace, path).toString(), "Entry not found");
        }
        return entries;
    }

    /**
     * checks if a collection already contains an entry, if that's the case an error is logged
     * @param collection collection to search in
     * @param entry entry to search for
     * @return is the entry contained in the given collection
     */
    protected final boolean isNotPresent(Collection<T> collection, T entry) {
        if (collection.contains(entry)) {
            log(Objects.requireNonNull(entry.getRegistryName()).toString(), "Already present");
            return false;
        }
        return true;
    }

    /**
     * log a warning when there is a problem with a certain entry
     * @param entry problematic entry
     * @param message message to print
     */
    protected static void log(String entry, String message) {
        PuzzlesLib.LOGGER.warn("Unable to parse entry {}: {}", entry, message);
    }
    
}