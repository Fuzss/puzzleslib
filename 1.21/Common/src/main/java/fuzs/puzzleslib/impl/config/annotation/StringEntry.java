package fuzs.puzzleslib.impl.config.annotation;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class StringEntry extends LimitedEntry<String> {

    public StringEntry(Field field) {
        super(field);
    }

    @Override
    public ModConfigSpec.ConfigValue<String> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
        return builder.define(this.getName(), this.getDefaultValue(o), this.getValidator());
    }
}
