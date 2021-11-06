package fuzs.puzzleslib.config.serialization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * builds a collection for a given type of registry from a list of strings
 * @param <T> content type of collection to build
 */
public class EntryCollectionBuilder<T extends IForgeRegistryEntry<T>> extends StringEntryReader<T> {
    /**
     * config builder comment with custom data after path, usually comma separated
     */
    public static final Function<String, String> CONFIG_STRING_BUILDER = s -> "Format for every entry is \"<namespace>:<path>" + s + "\". Path may use asterisk as wildcard parameter. Tags are not supported.";
    /**
     * default entry builder config comment
     */
    public static final String CONFIG_STRING = CONFIG_STRING_BUILDER.apply("");

    /**
     * @param registry registry entries the to be created collections contain
     */
    private EntryCollectionBuilder(IForgeRegistry<T> registry) {
        super(registry);
    }

    /**
     * @param locations resource locations to build set from
     * @return entry set associated with given resource locations in active registry
     */
    public Set<T> buildSet(List<String> locations) {
        return this.buildSet(locations, flag -> true, "");
    }

    /**
     * @param locations resource locations to build set from
     * @return entry map associated with given resource locations in active registry paired with a given double value
     */
    public Map<T, double[]> buildMap(List<String> locations) {
        return this.buildMap(locations, (entry, value) -> true, "");
    }

    /**
     * @param locations resource locations to build set from
     * @param condition condition need to match for an entry to be added to the set
     * @param errorMessage message to be logged when condition is not met
     * @return entry set associated with given resource locations in active registry
     */
    public Set<T> buildSet(List<String> locations, Predicate<T> condition, String errorMessage) {
        Set<T> set = Sets.newHashSet();
        for (String source : locations) {
            this.getEntriesFromRegistry(source.trim()).forEach(entry -> {
                if (condition.test(entry)) {
                    if (this.isNotPresent(set, entry)) {
                        set.add(entry);
                    }
                } else {
                    log(source, errorMessage);
                }
            });
        }
        return set;
    }

    /**
     * @param locations resource locations to build set from
     * @param condition condition need to match for an entry to be added to the map
     * @param errorMessage message to be logged when condition is not met
     * @return entry map associated with given resource locations in active registry paired with a given double value
     */
    public Map<T, double[]> buildMap(List<String> locations, BiPredicate<T, double[]> condition, String errorMessage) {
        Map<T, double[]> map = Maps.newHashMap();
        for (String source : locations) {
            String[] splitSource = Stream.of(source.split(",")).map(String::trim).toArray(String[]::new);
            if (splitSource.length == 0) {
                log(source, "Wrong number of arguments");
                continue;
            }
            List<T> entries = this.getEntriesFromRegistry(splitSource[0]);
            if (entries.isEmpty()) {
                continue;
            }
            double[] values = Stream.of(splitSource).skip(1).mapToDouble(value -> parseDouble(value, source)).toArray();
            for (T entry : entries) {
                if (condition.test(entry, values)) {
                    if (this.isNotPresent(map.keySet(), entry)) {
                        map.put(entry, values);
                    }
                } else {
                    log(source, errorMessage);
                }
            }
        }
        return map;
    }

    /**
     * @param value double or boolean to parse
     * @param source currently worked on entry for error message
     * @return parsed double
     */
    private static double parseDouble(String value, String source) {
        if (value.equalsIgnoreCase("true")) {
            return 1.0;
        } else if (value.equalsIgnoreCase("false")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            log(source, "Invalid number format");
        }
        return 0.0;
    }

    /**
     * @param registry registry for type
     * @param <T> registry type
     * @return builder backed by <code>registry</code>
     */
    public static <T extends IForgeRegistryEntry<T>> EntryCollectionBuilder<T> of(IForgeRegistry<T> registry) {
        return new EntryCollectionBuilder<>(registry);
    }
}
