package fuzs.puzzleslib.impl.config.serialization;

import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record RegistryProvider<T>(Registry<T> registry) implements KeyedValueProvider<T> {

    public RegistryProvider(ResourceKey<? extends Registry<? super T>> registryKey) {
        this(LookupHelper.getRegistry(registryKey).orElseThrow());
    }

    @Override
    public Optional<T> getValue(Identifier name) {
        return this.registry.getOptional(name);
    }

    @Override
    public Identifier getKey(T value) {
        return this.registry.getKey(value);
    }

    @Override
    public Stream<Map.Entry<Identifier, T>> stream() {
        return this.registry.entrySet().stream().map(entry -> Map.entry(entry.getKey().identifier(), entry.getValue()));
    }

    @Override
    public Stream<T> streamValues() {
        return this.registry.stream();
    }

    @Override
    public String name() {
        return this.registry.key().identifier().toString();
    }
}
