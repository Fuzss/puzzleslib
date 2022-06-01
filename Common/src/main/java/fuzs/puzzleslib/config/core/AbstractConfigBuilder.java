package fuzs.puzzleslib.config.core;

import com.electronwill.nightconfig.core.EnumGetMethod;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * an abstraction of ForgeConfigSpec.Builder to make it usable in a common project without any attached mod loader
 * probably easier than attaching the whole forge config api to the project and having to worry about loader dependent classes...
 */
public interface AbstractConfigBuilder {
    
    //Object
    <T> Supplier<T> define(String path, T defaultValue);

    <T> Supplier<T> define(List<String> path, T defaultValue);

    <T> Supplier<T> define(String path, T defaultValue, Predicate<Object> validator);

    <T> Supplier<T> define(List<String> path, T defaultValue, Predicate<Object> validator);

    <T> Supplier<T> define(String path, Supplier<T> defaultSupplier, Predicate<Object> validator);

    <T> Supplier<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator);

    <T> Supplier<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz);

//    <T> Supplier<T> define(List<String> path, ForgeConfigSpec.ValueSpec value, Supplier<T> defaultSupplier);

    <V extends Comparable<? super V>> Supplier<V> defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz);

    <V extends Comparable<? super V>> Supplier<V> defineInRange(List<String> path, V defaultValue, V min, V max, Class<V> clazz);

    <V extends Comparable<? super V>> Supplier<V> defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz);

    <V extends Comparable<? super V>> Supplier<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz);

    <T> Supplier<T> defineInList(String path, T defaultValue, Collection<? extends T> acceptableValues);

    <T> Supplier<T> defineInList(String path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues);

    <T> Supplier<T> defineInList(List<String> path, T defaultValue, Collection<? extends T> acceptableValues);

    <T> Supplier<T> defineInList(List<String> path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues);

    <T> Supplier<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator);

    <T> Supplier<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator);

    <T> Supplier<List<? extends T>> defineList(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator);

    <T> Supplier<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator);

    <T> Supplier<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator);

    //Enum
    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, @SuppressWarnings("unchecked") V... acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, @SuppressWarnings("unchecked") V... acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, @SuppressWarnings("unchecked") V... acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, @SuppressWarnings("unchecked") V... acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, Collection<V> acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues);

    @SuppressWarnings("unchecked")
    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, Predicate<Object> validator);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, Predicate<Object> validator);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz);

    <V extends Enum<V>> Supplier<V> defineEnum(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz);

    <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz);

    //boolean
    Supplier<Boolean> define(String path, boolean defaultValue);

    Supplier<Boolean> define(List<String> path, boolean defaultValue);

    Supplier<Boolean> define(String path, Supplier<Boolean> defaultSupplier);

    Supplier<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier);

    //Double
    Supplier<Double> defineInRange(String path, double defaultValue, double min, double max);

    Supplier<Double> defineInRange(List<String> path, double defaultValue, double min, double max);

    Supplier<Double> defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max);

    Supplier<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max);

    //Ints
    Supplier<Integer> defineInRange(String path, int defaultValue, int min, int max);

    Supplier<Integer> defineInRange(List<String> path, int defaultValue, int min, int max);

    Supplier<Integer> defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max);

    Supplier<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max);

    //Longs
    Supplier<Long> defineInRange(String path, long defaultValue, long min, long max);

    Supplier<Long> defineInRange(List<String> path, long defaultValue, long min, long max);

    Supplier<Long> defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max);

    Supplier<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max);

    AbstractConfigBuilder comment(String comment);

    AbstractConfigBuilder comment(String... comment);

    AbstractConfigBuilder translation(String translationKey);

    AbstractConfigBuilder worldRestart();

    AbstractConfigBuilder push(String path);

    AbstractConfigBuilder push(List<String> path);

    AbstractConfigBuilder pop();

    AbstractConfigBuilder pop(int count);
}
