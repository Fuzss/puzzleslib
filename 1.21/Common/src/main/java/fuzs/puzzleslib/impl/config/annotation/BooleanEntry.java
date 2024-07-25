package fuzs.puzzleslib.impl.config.annotation;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class BooleanEntry extends ValueEntry<Boolean> {

    public BooleanEntry(Field field) {
        super(field);
    }

    @Override
    public ModConfigSpec.BooleanValue getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
        return builder.define(this.getName(), (boolean) this.getDefaultValue(o));
    }
}
