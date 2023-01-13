package fuzs.puzzleslib.core;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * implementation of {@link ModLoaderEnvironment} for Forge
 */
@SuppressWarnings("UnstableApiUsage")
public final class ForgeEnvironment implements ModLoaderEnvironment {

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
        Objects.requireNonNull(ModList.get(), "mod list is null, use isModLoadedSafe instead");
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isModLoadedSafe(String modId) {
        Objects.requireNonNull(FMLLoader.getLoadingModList(), "loading mod list is null");
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public Optional<String> getModName(String modId) {
        Objects.requireNonNull(ModList.get(), "mod list is null");
        return ModList.get().getModContainerById(modId).map(ModContainer::getModInfo).map(IModInfo::getDisplayName);
    }
}