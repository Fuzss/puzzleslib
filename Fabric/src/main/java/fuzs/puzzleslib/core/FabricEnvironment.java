package fuzs.puzzleslib.core;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricEnvironment implements ModLoaderEnvironment {

    @Override
    public DistType getEnvironmentType() {
        return DistTypeConverter.fromEnvType(FabricLoader.getInstance().getEnvironmentType());
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}