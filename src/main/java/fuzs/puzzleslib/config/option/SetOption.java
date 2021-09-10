package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SetOption<T> extends CollectionOption<T, Set<T>> {

    SetOption(ForgeConfigSpec.ConfigValue<List<? extends String>> value, ModConfig.Type type, SetOptionBuilder<T> builder) {

        super(value, type, builder);
    }

    @Override
    Collector<? super T, ?, Set<T>> collect() {
        
        return Collectors.toSet();
    }

    public static class SetOptionBuilder<T> extends CollectionOptionBuilder<T, Set<T>> {
        
        private final Function<T, String> valueToName;
        private final Function<String, T> nameToValue;

        SetOptionBuilder(OptionBuilder previous, String name, Set<T> defaultValue, Function<T, String> valueToName, Function<String, T> nameToValue) {

            super(previous, name, defaultValue);
            this.valueToName = valueToName;
            this.nameToValue = nameToValue;
        }

        @Override
        CollectionOption<T, Set<T>> createOption(ForgeConfigSpec.ConfigValue<List<? extends String>> value, ModConfig.Type type) {

            return new SetOption<>(value, type, this);
        }

        @Override
        String valueToName(T value) {
            
            return this.valueToName.apply(value);
        }

        @Override
        T nameToValue(String name) {
            
            return this.nameToValue.apply(name);
        }
        
    }

}
