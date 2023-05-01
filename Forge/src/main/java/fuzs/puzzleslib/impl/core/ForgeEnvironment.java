package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.DistType;
import fuzs.puzzleslib.api.core.v1.ForgeDistTypeConverter;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * implementation of {@link ModLoaderEnvironment} for Forge
 */
public final class ForgeEnvironment implements ModLoaderEnvironment {

    @Override
    public ModLoader getModLoader() {
        return ModLoader.FORGE;
    }

    @Override
    public DistType getEnvironmentType() {
        return ForgeDistTypeConverter.fromDist(FMLEnvironment.dist);
    }

    @Override
    public Path getGameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getModsDirectory() {
        return FMLPaths.MODSDIR.get();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Optional<Path> findModResource(String id, String... pathName) {
        ModList modList = ModList.get();
        Objects.requireNonNull(modList, "mod list is null");
        return Optional.of(modList.getModFileById(id).getFile().findResource(pathName)).filter(Files::exists);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        Objects.requireNonNull(modList, "mod list is null");
        return modList.isLoaded(modId);
    }

    @Override
    public boolean isModLoadedSafe(String modId) {
        Objects.requireNonNull(FMLLoader.getLoadingModList(), "mod loading list is null");
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public Optional<String> getModName(String modId) {
        ModList modList = ModList.get();
        Objects.requireNonNull(modList, "mod list is null");
        return modList.getModContainerById(modId).map(ModContainer::getModInfo).map(IModInfo::getDisplayName);
    }
}