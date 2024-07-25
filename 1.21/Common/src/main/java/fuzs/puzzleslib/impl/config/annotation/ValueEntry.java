package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigTranslationsManager;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Consumer;

public abstract class ValueEntry<T> extends ConfigEntry<T> {

    public ValueEntry(Field field) {
        super(field);
    }

    @Override
    public final void defineValue(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @Nullable Object o) {
        // final fields are permitted until here, since values must be able to change
        // previously only new config categories are handled, those instances never change
        if (Modifier.isFinal(this.field.getModifiers())) throw new RuntimeException("Field may not be final");
        List<String> comments = this.getComments();
        builder.comment(comments.toArray(String[]::new));
        if (this.getAnnotation().worldRestart()) builder.worldRestart();
        ModConfigSpec.ConfigValue<T> configValue = this.getConfigValue(builder, o);
        context.accept(configValue, this.getValueCallback(configValue, o));
        ConfigTranslationsManager.INSTANCE.addConfigValue(context.getModId(), configValue.getPath());
        ConfigTranslationsManager.INSTANCE.addConfigValueComment(context.getModId(), configValue.getPath(), comments);
    }

    public abstract ModConfigSpec.ConfigValue<T> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o);

    private Consumer<T> getValueCallback(ModConfigSpec.ConfigValue<T> configValue, @Nullable Object o) {
        try {
            MethodHandle methodHandle = MethodHandles.lookup().unreflectSetter(this.field);
            return (T value) -> {
                try {
                    methodHandle.invoke(o, configValue.get());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            };
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
