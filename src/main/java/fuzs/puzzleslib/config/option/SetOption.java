package fuzs.puzzleslib.config.option;

import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.Set;

public class SetOption<T> extends CollectionOption<T, Set<T>> {

    SetOption(ForgeConfigSpec.ConfigValue<List<? extends T>> value, ModConfig.Type type, SetOptionBuilder<T> builder) {

        super(value, type, builder);
    }

    @Override
    protected Set<T> convertValue(List<? extends T> value) {

        return Sets.newHashSet(value);
    }

    public static class SetOptionBuilder<T> extends CollectionOptionBuilder<T, Set<T>> {

        SetOptionBuilder(OptionBuilder previous, String name, Set<T> defaultValue) {

            super(previous, name, defaultValue);
        }

        @Override
        CollectionOption<T, Set<T>> createOption(ForgeConfigSpec.ConfigValue<List<? extends T>> value, ModConfig.Type type) {

            return new SetOption<>(value, type, this);
        }
    }

}
