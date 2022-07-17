package fuzs.puzzleslib.core;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * implementation of {@link ModLoaderEnvironment} for Forge
 */
public class ForgeEnvironment implements ModLoaderEnvironment {

    @Override
    public ModLoader getModLoader() {
        return ModLoader.FORGE;
    }

    @Override
    public DistType getEnvironmentType() {
        return DistTypeConverter.fromDist(FMLEnvironment.dist);
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isModLoadedSafe(String modId) {
        return FMLLoader.getLoadingModList() == null || FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}