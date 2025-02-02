package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.api.config.v3.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public abstract class NumberEntry<T extends Number, A extends Annotation> extends ValueEntry<T> {
    private final Class<A> rangeClazz;

    public NumberEntry(Field field, Class<A> rangeClazz) {
        super(field);
        this.rangeClazz = rangeClazz;
    }

    public Optional<A> getRangeAnnotation() {
        return Optional.ofNullable(this.field.getDeclaredAnnotation(this.rangeClazz));
    }

    public abstract T min();

    public abstract T max();

    public static final class IntegerEntry extends NumberEntry<Integer, Config.IntRange> {

        public IntegerEntry(Field field) {
            super(field, Config.IntRange.class);
        }

        @Override
        public ModConfigSpec.IntValue getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            return builder.defineInRange(this.getName(), this.getDefaultValue(o), this.min(), this.max());
        }

        @Override
        public Integer min() {
            return this.getRangeAnnotation().map(Config.IntRange::min).orElse(Integer.MIN_VALUE);
        }

        @Override
        public Integer max() {
            return this.getRangeAnnotation().map(Config.IntRange::max).orElse(Integer.MAX_VALUE);
        }
    }

    public static final class LongEntry extends NumberEntry<Long, Config.LongRange> {

        public LongEntry(Field field) {
            super(field, Config.LongRange.class);
        }

        @Override
        public ModConfigSpec.LongValue getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            return builder.defineInRange(this.getName(), this.getDefaultValue(o), this.min(), this.max());
        }

        @Override
        public Long min() {
            return this.getRangeAnnotation().map(Config.LongRange::min).orElse(Long.MIN_VALUE);
        }

        @Override
        public Long max() {
            return this.getRangeAnnotation().map(Config.LongRange::max).orElse(Long.MAX_VALUE);
        }
    }

    public static final class DoubleEntry extends NumberEntry<Double, Config.DoubleRange> {

        public DoubleEntry(Field field) {
            super(field, Config.DoubleRange.class);
        }

        @Override
        public ModConfigSpec.DoubleValue getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            return builder.defineInRange(this.getName(), this.getDefaultValue(o), this.min(), this.max());
        }

        @Override
        public Double min() {
            return this.getRangeAnnotation().map(Config.DoubleRange::min).orElse(Double.MIN_VALUE);
        }

        @Override
        public Double max() {
            return this.getRangeAnnotation().map(Config.DoubleRange::max).orElse(Double.MAX_VALUE);
        }
    }
}
