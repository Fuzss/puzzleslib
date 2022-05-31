package fuzs.puzzleslib.core;

import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.puzzleslib.registry.FuelManager;

import java.util.ServiceLoader;

public class Services {
    public static final ModLoaderEnvironment ENVIRONMENT = load(ModLoaderEnvironment.class);
    public static final Factories FACTORIES = load(Factories.class);
    public static final FuelManager FUEL_MANAGER = load(FuelManager.class);

    private static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}