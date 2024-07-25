package fuzs.puzzleslib.impl.config.annotation;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class EnumEntry<E extends Enum<E>> extends LimitedEntry<E> {

    public EnumEntry(Field field) {
        super(field);
    }

    @Override
    public ModConfigSpec.EnumValue<E> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
        return builder.defineEnum(this.getName(), this.getDefaultValue(o), this.getValidator());
    }
}
