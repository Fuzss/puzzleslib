package fuzs.puzzleslib.config.option;

import fuzs.puzzleslib.config.ConfigManager;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

public class RegistryMapOption<T extends IForgeRegistryEntry<T>> extends CollectionOption<T, Map<T, double[]>> {

    private final IForgeRegistry<T> registry;

    RegistryMapOption(ForgeConfigSpec.ConfigValue<List<? extends String>> value, ModConfig.Type type, RegistrySetOptionBuilder<T> builder) {

        super(value, type, builder);
        this.registry = builder.registry;
    }

    @SuppressWarnings("unchecked")
    @Override
    Set<T> convertValue(List<? extends String> value) {

        return ConfigManager.deserializeToSet((List<String>) value, this.registry);
    }

    @Override
    Collector<? super T, ?, Set<T>> collect() {

        throw new UnsupportedOperationException();
    }

    public static class RegistrySetOptionBuilder<T extends IForgeRegistryEntry<T>> extends CollectionOptionBuilder<T, Map<T>> {

        private final IForgeRegistry<T> registry;

        RegistrySetOptionBuilder(OptionBuilder previous, String name, Map<T> defaultValue, IForgeRegistry<T> registry) {

            super(previous, name, defaultValue);
            this.registry = registry;
        }

        @Override
        List<String> buildComment() {

            List<String> comment = super.buildComment();
            comment.add(EntryCollectionBuilder.CONFIG_STRING);
            return comment;
        }

        @Override
        CollectionOption<T, Set<T>> createOption(ForgeConfigSpec.ConfigValue<List<? extends String>> value, ModConfig.Type type) {

            return new RegistryMapOption<>(value, type, this);
        }

        @Override
        String valueToName(T value) {

            return value.getRegistryName().toString();
        }

        @Override
        T nameToValue(String name) {

            throw new UnsupportedOperationException();
        }

    }

}
