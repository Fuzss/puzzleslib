package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.api.config.v3.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class LongEntry extends NumberEntry<Long, Config.LongRange> {

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
