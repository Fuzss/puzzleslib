package fuzs.puzzleslib.fabric.impl.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FabricEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        return FabricLoader.getInstance().getAllMods().stream()
                .map(FabricModContainer::new)
                .sorted(Comparator.comparing(ModContainer::getModId))
                // compiler cannot infer type arguments here
                .collect(ImmutableMap.<FabricModContainer, String, ModContainer>toImmutableMap(ModContainer::getModId, Function.identity()));
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
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment() && System.getProperty("fabric-api.datagen") == null;
    }

    @Override
    public boolean isDataGeneration() {
        return System.getProperty("fabric-api.datagen") != null;
    }

    @Override
    public Map<String, ModContainer> getModList() {
        return this.modList.get();
    }

    @Override
    public ObjectShareAccess getObjectShareAccess() {
        return FabricObjectShareAccess.INSTANCE;
    }
}