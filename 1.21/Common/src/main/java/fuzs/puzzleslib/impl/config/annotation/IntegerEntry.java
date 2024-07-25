package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.api.config.v3.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class IntegerEntry extends NumberEntry<Integer, Config.IntRange> {

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
