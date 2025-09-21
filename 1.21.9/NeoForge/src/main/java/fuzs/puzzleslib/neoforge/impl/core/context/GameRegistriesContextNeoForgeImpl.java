package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.GameRegistriesContext;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.Objects;

public record GameRegistriesContextNeoForgeImpl(NewRegistryEvent event) implements GameRegistriesContext {

    @Override
    public <T> void registerRegistry(Registry<T> registry) {
        Objects.requireNonNull(registry, "registry is null");
        this.event.register(registry);
    }
}
