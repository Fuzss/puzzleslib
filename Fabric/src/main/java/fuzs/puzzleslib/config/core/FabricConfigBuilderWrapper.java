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
 *
 * @param builder   the wrapped {@link ForgeConfigSpec.Builder}
 */
public record FabricConfigBuilderWrapper(ForgeConfigSpec.Builder builder) implements AbstractConfigBuilder {

    @Override
    public <T> AbstractConfigValue<T> define(String path, T defaultValue) {
        return wrap(this.builder.define(path, defaultValue));
    }

    @Override
    public <T> AbstractConfigValue<T> define(List<String> path, T defaultValue) {
        return wrap(this.builder.define(path, defaultValue));
    }

    @Override
    public <T> AbstractConfigValue<T> define(String path, T defaultValue, Predicate<Object> validator) {
        return wrap(this.builder.define(path, defaultValue, validator));
    }

    @Override
    public <T> AbstractConfigValue<T> define(List<String> path, T defaultValue, Predicate<Object> validator) {
        return wrap(this.builder.define(path, defaultValue, validator));
    }

    @Override
    public <T> AbstractConfigValue<T> define(String path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return wrap(this.builder.define(path, defaultSupplier, validator));
    }

    @Override
    public <T> AbstractConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return wrap(this.builder.define(path, defaultSupplier, validator));
    }

    @Override
    public <T> AbstractConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
        return wrap(this.builder.define(path, defaultSupplier, validator, clazz));
    }

//    @Override
//    public <T> AbstractConfigValue<T> define(List<String> path, ForgeConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) {
//        return null;
//    }

    @Override
    public <V extends Comparable<? super V>> AbstractConfigValue<V> defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max, clazz));
    }

    @Override
    public <V extends Comparable<? super V>> AbstractConfigValue<V> defineInRange(List<String> path, V defaultValue, V min, V max, Class<V> clazz) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max, clazz));
    }

    @Override
    public <V extends Comparable<? super V>> AbstractConfigValue<V> defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max, clazz));
    }

    @Override
    public <V extends Comparable<? super V>> AbstractConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max, clazz));
    }

    @Override
    public <T> AbstractConfigValue<T> defineInList(String path, T defaultValue, Collection<? extends T> acceptableValues) {
        return wrap(this.builder.defineInList(path, defaultValue, acceptableValues));
    }

    @Override
    public <T> AbstractConfigValue<T> defineInList(String path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
        return wrap(this.builder.defineInList(path, defaultSupplier, acceptableValues));
    }

    @Override
    public <T> AbstractConfigValue<T> defineInList(List<String> path, T defaultValue, Collection<? extends T> acceptableValues) {
        return wrap(this.builder.defineInList(path, defaultValue, acceptableValues));
    }

    @Override
    public <T> AbstractConfigValue<T> defineInList(List<String> path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
        return wrap(this.builder.defineInList(path, defaultSupplier, acceptableValues));
    }

    @Override
    public <T> AbstractConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return wrap(this.builder.defineList(path, defaultValue, elementValidator));
    }

    @Override
    public <T> AbstractConfigValue<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return wrap(this.builder.defineList(path, defaultSupplier, elementValidator));
    }

    @Override
    public <T> AbstractConfigValue<List<? extends T>> defineList(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return wrap(this.builder.defineList(path, defaultValue, elementValidator));
    }

    @Override
    public <T> AbstractConfigValue<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return wrap(this.builder.defineList(path, defaultSupplier, elementValidator));
    }

    @Override
    public <T> AbstractConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return wrap(this.builder.defineListAllowEmpty(path, defaultSupplier, elementValidator));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue) {
        return wrap(this.builder.defineEnum(path, defaultValue));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue) {
        return wrap(this.builder.defineEnum(path, defaultValue));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter));
    }

    @SafeVarargs
    @Override
    public final <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, V... acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, acceptableValues));
    }

    @SafeVarargs
    @Override
    public final <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter, acceptableValues));
    }

    @SafeVarargs
    @Override
    public final <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, V... acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, acceptableValues));
    }

    @SafeVarargs
    @Override
    public final <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter, acceptableValues));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, Collection<V> acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, acceptableValues));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter, acceptableValues));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, acceptableValues));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter, acceptableValues));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, Predicate<Object> validator) {
        return wrap(this.builder.defineEnum(path, defaultValue, validator));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter, validator));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, Predicate<Object> validator) {
        return wrap(this.builder.defineEnum(path, defaultValue, validator));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return wrap(this.builder.defineEnum(path, defaultValue, converter, validator));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return wrap(this.builder.defineEnum(path, defaultSupplier, validator, clazz));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return wrap(this.builder.defineEnum(path, defaultSupplier, converter, validator, clazz));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return wrap(this.builder.defineEnum(path, defaultSupplier, validator, clazz));
    }

    @Override
    public <V extends Enum<V>> AbstractConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return wrap(this.builder.defineEnum(path, defaultSupplier, converter, validator, clazz));
    }

    @Override
    public AbstractConfigValue<Boolean> define(String path, boolean defaultValue) {
        return wrap(this.builder.define(path, defaultValue));
    }

    @Override
    public AbstractConfigValue<Boolean> define(List<String> path, boolean defaultValue) {
        return wrap(this.builder.define(path, defaultValue));
    }

    @Override
    public AbstractConfigValue<Boolean> define(String path, Supplier<Boolean> defaultSupplier) {
        return wrap(this.builder.define(path, defaultSupplier));
    }

    @Override
    public AbstractConfigValue<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier) {
        return wrap(this.builder.define(path, defaultSupplier));
    }

    @Override
    public AbstractConfigValue<Double> defineInRange(String path, double defaultValue, double min, double max) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max));
    }

    @Override
    public AbstractConfigValue<Double> defineInRange(List<String> path, double defaultValue, double min, double max) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max));
    }

    @Override
    public AbstractConfigValue<Double> defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max));
    }

    @Override
    public AbstractConfigValue<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max));
    }

    @Override
    public AbstractConfigValue<Integer> defineInRange(String path, int defaultValue, int min, int max) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max));
    }

    @Override
    public AbstractConfigValue<Integer> defineInRange(List<String> path, int defaultValue, int min, int max) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max));
    }

    @Override
    public AbstractConfigValue<Integer> defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max));
    }

    @Override
    public AbstractConfigValue<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max));
    }

    @Override
    public AbstractConfigValue<Long> defineInRange(String path, long defaultValue, long min, long max) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max));
    }

    @Override
    public AbstractConfigValue<Long> defineInRange(List<String> path, long defaultValue, long min, long max) {
        return wrap(this.builder.defineInRange(path, defaultValue, min, max));
    }

    @Override
    public AbstractConfigValue<Long> defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max));
    }

    @Override
    public AbstractConfigValue<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
        return wrap(this.builder.defineInRange(path, defaultSupplier, min, max));
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

    /**
     * @param value     value to wrap
     * @param <T>       value data type
     * @return          wrapped value
     */
    private static <T> AbstractConfigValue<T> wrap(ForgeConfigSpec.ConfigValue<T> value) {
        return new FabricConfigValueWrapper<>(value);
    }
}
