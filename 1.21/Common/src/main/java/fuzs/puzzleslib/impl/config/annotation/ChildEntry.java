package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.AnnotatedConfigBuilder;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class ChildEntry extends ConfigEntry<ConfigCore> {

    public ChildEntry(Field field) {
        super(field);
    }

    @Override
    public void defineValue(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @Nullable Object o) {
        builder.comment(this.getComments().toArray(String[]::new));
        builder.push(this.getName());
        AnnotatedConfigBuilder.serialize(builder, context, (Class<? extends ConfigCore>) this.field.getType(), this.getDefaultValue(o));
        builder.pop();
    }
}
