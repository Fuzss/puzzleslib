package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.api.config.v3.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class DoubleEntry extends NumberEntry<Double, Config.DoubleRange> {

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
