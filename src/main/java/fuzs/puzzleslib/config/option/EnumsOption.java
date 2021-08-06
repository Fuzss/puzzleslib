package fuzs.puzzleslib.config.option;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.Set;

public class EnumsOption<T extends Enum<T>> extends SetOption<T> {

    private final Class<T> declaringClazz;

    EnumsOption(ForgeConfigSpec.ConfigValue<List<? extends T>> value, ModConfig.Type type, EnumsOptionBuilder<T> builder) {

        super(value, type, builder);
        this.declaringClazz = builder.declaringClazz;
    }

    public Class<T> getDeclaringClass() {

        return this.declaringClazz;
    }

    public static class EnumsOptionBuilder<T extends Enum<T>> extends SetOptionBuilder<T> {

        private final Class<T> declaringClazz;

        EnumsOptionBuilder(OptionBuilder previous, String name, Set<T> defaultValue, Class<T> declaringClazz) {

            super(previous, name, defaultValue);
            this.declaringClazz = declaringClazz;
            this.acceptableValues = Lists.newArrayList(declaringClazz.getEnumConstants());
        }

        @Override
        CollectionOption<T, Set<T>> createOption(ForgeConfigSpec.ConfigValue<List<? extends T>> value, ModConfig.Type type) {

            return new EnumsOption<>(value, type, this);
        }
    }

}
