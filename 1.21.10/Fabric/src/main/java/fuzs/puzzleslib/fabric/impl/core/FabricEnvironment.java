package fuzs.puzzleslib.fabric.impl.core;

import com.google.common.base.Suppliers;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public final class FabricEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        return ModContainer.toModList(FabricModContainer::getFabricModContainers);
    });

    @Override
    public ModLoader getModLoader() {
        return FabricLoader.getInstance().isModLoaded("quilt_loader") ? ModLoader.QUILT : ModLoader.FABRIC;
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getModsDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getCurrentMappingsNamespace() {
        return FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isDataGeneration() {
        return System.getProperty("fabric-api.datagen") != null;
    }

    @Override
    public Map<String, ModContainer> getModList() {
        return this.modList.get();
    }
}
