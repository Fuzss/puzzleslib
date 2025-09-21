package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.GameRegistriesContext;
import net.minecraft.core.Registry;

import java.util.Objects;

public final class GameRegistriesContextFabricImpl implements GameRegistriesContext {

    @Override
    public <T> void registerRegistry(Registry<T> registry) {
        Objects.requireNonNull(registry, "registry is null");
        // new registries are directly registered on Fabric upon construction
    }
}
