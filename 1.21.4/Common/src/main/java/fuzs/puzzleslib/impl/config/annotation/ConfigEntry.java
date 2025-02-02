package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.base.CaseFormat;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ConfigEntry<T> {
    final Field field;

    public ConfigEntry(Field field) {
        this.field = field;
    }

    public String getName() {
        // get the name from the config, often this is left blank, so instead we create it from the field's name with an underscore format
        if (StringUtils.isBlank(this.getAnnotation().name())) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.field.getName());
        } else {
            return this.getAnnotation().name();
        }
    }

    public List<String> getComments(@Nullable Object o) {
        return new ArrayList<>(Arrays.asList(this.getAnnotation().description()));
    }

    public T getDefaultValue(@Nullable Object o) {
        try {
            return (T) this.field.get(o);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Config getAnnotation() {
        return this.field.getAnnotation(Config.class);
    }

    public abstract void defineValue(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @Nullable Object o);

    public static final class ChildEntry extends ConfigEntry<ConfigCore> {

        public ChildEntry(Field field) {
            super(field);
        }

        @Override
        public void defineValue(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @Nullable Object o) {
            List<String> comments = this.getComments(o);
            builder.comment(comments.toArray(String[]::new));
            builder.push(this.getName());
            ConfigBuilder.build(builder, context, (Class<? extends ConfigCore>) this.field.getType(), this.getDefaultValue(o));
            builder.pop();
        }
    }
}
