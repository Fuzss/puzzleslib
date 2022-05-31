package fuzs.puzzleslib.config.core;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * implementation of {@link AbstractConfigBuilder}
 * same on Forge and Fabric, but may not exist in common project due to it depending on {@link ForgeConfigSpec.Builder}
 * the wrapper misses out on {@link ForgeConfigSpec.Builder#build()} and {@link ForgeConfigSpec.Builder#configure},
 * these actions can still be performed in the mod loader specific projects though directly on the wrapped builder
 * (store it before creating the wrapper)
 */
public class FabricConfigBuilderWrapper implements AbstractConfigBuilder {
    /**
     * the wrapped {@link ForgeConfigSpec.Builder}
     */
    private final ForgeConfigSpec.Builder builder;

    /**
     * @param builder create new wrapper from builder
     */
    public FabricConfigBuilderWrapper(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
    }

    @Override
    public <T> Supplier<T> define(String path, T defaultValue) {
        return this.builder.define(path, defaultValue)::get;
    }

    @Override
    public <T> Supplier<T> define(List<String> path, T defaultValue) {
        return this.builder.define(path, defaultValue)::get;
    }

    @Override
    public <T> Supplier<T> define(String path, T defaultValue, Predicate<Object> validator) {
        return this.builder.define(path, defaultValue, validator)::get;
    }

    @Override
    public <T> Supplier<T> define(List<String> path, T defaultValue, Predicate<Object> validator) {
        return this.builder.define(path, defaultValue, validator)::get;
    }

    @Override
    public <T> Supplier<T> define(String path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return this.builder.define(path, defaultSupplier, validator)::get;
    }

    @Override
    public <T> Supplier<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return this.builder.define(path, defaultSupplier, validator)::get;
    }

    @Override
    public <T> Supplier<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
        return this.builder.define(path, defaultSupplier, validator, clazz)::get;
    }

//    @Override
//    public <T> Supplier<T> define(List<String> path, ForgeConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) {
//        return null;
//    }

    @Override
    public <V extends Comparable<? super V>> Supplier<V> defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
        return this.builder.defineInRange(path, defaultValue, min, max, clazz)::get;
    }

    @Override
    public <V extends Comparable<? super V>> Supplier<V> defineInRange(List<String> path, V defaultValue, V min, V max, Class<V> clazz) {
        return this.builder.defineInRange(path, defaultValue, min, max, clazz)::get;
    }

    @Override
    public <V extends Comparable<? super V>> Supplier<V> defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return this.builder.defineInRange(path, defaultSupplier, min, max, clazz)::get;
    }

    @Override
    public <V extends Comparable<? super V>> Supplier<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return this.builder.defineInRange(path, defaultSupplier, min, max, clazz)::get;
    }

    @Override
    public <T> Supplier<T> defineInList(String path, T defaultValue, Collection<? extends T> acceptableValues) {
        return this.builder.defineInList(path, defaultValue, acceptableValues)::get;
    }

    @Override
    public <T> Supplier<T> defineInList(String path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
        return this.builder.defineInList(path, defaultSupplier, acceptableValues)::get;
    }

    @Override
    public <T> Supplier<T> defineInList(List<String> path, T defaultValue, Collection<? extends T> acceptableValues) {
        return this.builder.defineInList(path, defaultValue, acceptableValues)::get;
    }

    @Override
    public <T> Supplier<T> defineInList(List<String> path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
        return this.builder.defineInList(path, defaultSupplier, acceptableValues)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return this.builder.defineList(path, defaultValue, elementValidator)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return this.builder.defineList(path, defaultSupplier, elementValidator)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return this.builder.defineList(path, defaultValue, elementValidator)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return this.builder.defineList(path, defaultSupplier, elementValidator)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return this.builder.defineListAllowEmpty(path, defaultSupplier, elementValidator)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue) {
        return this.builder.defineEnum(path, defaultValue)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter) {
        return this.builder.defineEnum(path, defaultValue, converter)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue) {
        return this.builder.defineEnum(path, defaultValue)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter) {
        return this.builder.defineEnum(path, defaultValue, converter)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, V... acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, converter, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, V... acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, converter, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, Collection<V> acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, converter, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return this.builder.defineEnum(path, defaultValue, converter, acceptableValues)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, Predicate<Object> validator) {
        return this.builder.defineEnum(path, defaultValue, validator)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return this.builder.defineEnum(path, defaultValue, converter, validator)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, Predicate<Object> validator) {
        return this.builder.defineEnum(path, defaultValue, validator)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return this.builder.defineEnum(path, defaultValue, converter, validator)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return this.builder.defineEnum(path, defaultSupplier, validator, clazz)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return this.builder.defineEnum(path, defaultSupplier, converter, validator, clazz)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return this.builder.defineEnum(path, defaultSupplier, validator, clazz)::get;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return this.builder.defineEnum(path, defaultSupplier, converter, validator, clazz)::get;
    }

    @Override
    public Supplier<Boolean> define(String path, boolean defaultValue) {
        return this.builder.define(path, defaultValue)::get;
    }

    @Override
    public Supplier<Boolean> define(List<String> path, boolean defaultValue) {
        return this.builder.define(path, defaultValue)::get;
    }

    @Override
    public Supplier<Boolean> define(String path, Supplier<Boolean> defaultSupplier) {
        return this.builder.define(path, defaultSupplier)::get;
    }

    @Override
    public Supplier<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier) {
        return this.builder.define(path, defaultSupplier)::get;
    }

    @Override
    public Supplier<Double> defineInRange(String path, double defaultValue, double min, double max) {
        return this.builder.defineInRange(path, defaultValue, min, max)::get;
    }

    @Override
    public Supplier<Double> defineInRange(List<String> path, double defaultValue, double min, double max) {
        return this.builder.defineInRange(path, defaultValue, min, max)::get;
    }

    @Override
    public Supplier<Double> defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max) {
        return this.builder.defineInRange(path, defaultSupplier, min, max)::get;
    }

    @Override
    public Supplier<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
        return this.builder.defineInRange(path, defaultSupplier, min, max)::get;
    }

    @Override
    public Supplier<Integer> defineInRange(String path, int defaultValue, int min, int max) {
        return this.builder.defineInRange(path, defaultValue, min, max)::get;
    }

    @Override
    public Supplier<Integer> defineInRange(List<String> path, int defaultValue, int min, int max) {
        return this.builder.defineInRange(path, defaultValue, min, max)::get;
    }

    @Override
    public Supplier<Integer> defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max) {
        return this.builder.defineInRange(path, defaultSupplier, min, max)::get;
    }

    @Override
    public Supplier<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
        return this.builder.defineInRange(path, defaultSupplier, min, max)::get;
    }

    @Override
    public Supplier<Long> defineInRange(String path, long defaultValue, long min, long max) {
        return this.builder.defineInRange(path, defaultValue, min, max)::get;
    }

    @Override
    public Supplier<Long> defineInRange(List<String> path, long defaultValue, long min, long max) {
        return this.builder.defineInRange(path, defaultValue, min, max)::get;
    }

    @Override
    public Supplier<Long> defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max) {
        return this.builder.defineInRange(path, defaultSupplier, min, max)::get;
    }

    @Override
    public Supplier<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
        return this.builder.defineInRange(path, defaultSupplier, min, max)::get;
    }

    @Override
    public AbstractConfigBuilder comment(String comment) {
        this.builder.comment(comment);
        return this;
    }

    @Override
    public AbstractConfigBuilder comment(String... comment) {
        this.builder.comment(comment);
        return this;
    }

    @Override
    public AbstractConfigBuilder translation(String translationKey) {
        this.builder.translation(translationKey);
        return this;
    }

    @Override
    public AbstractConfigBuilder worldRestart() {
        this.builder.worldRestart();
        return this;
    }

    @Override
    public AbstractConfigBuilder push(String path) {
        this.builder.push(path);
        return this;
    }

    @Override
    public AbstractConfigBuilder push(List<String> path) {
        this.builder.push(path);
        return this;
    }

    @Override
    public AbstractConfigBuilder pop() {
        this.builder.pop();
        return this;
    }

    @Override
    public AbstractConfigBuilder pop(int count) {
        this.builder.pop(count);
        return this;
    }
}
