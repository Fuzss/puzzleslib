package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import fuzs.puzzleslib.impl.core.EmptyObjectShareAccessImpl;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class NeoForgeEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        List<? extends IModInfo> modList = getForgeModList();
        return modList.stream()
                .map(NeoForgeModContainer::new)
                .sorted(Comparator.comparing(ModContainer::getModId))
                // compiler cannot infer type arguments here
                .collect(ImmutableMap.<NeoForgeModContainer, String, ModContainer>toImmutableMap(ModContainer::getModId, Function.identity()));
    });

    private static List<? extends IModInfo> getForgeModList() {
        if (ModList.get() != null) {
            return ModList.get().getMods();
        } else if (FMLLoader.getLoadingModList() != null) {
            return FMLLoader.getLoadingModList().getMods();
        } else {
            throw new NullPointerException("mod list is null");
        }
    }

    @Override
    public ModLoader getModLoader() {
        return ModLoader.NEOFORGE;
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public boolean isServer() {
        return FMLEnvironment.dist.isDedicatedServer();
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
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public boolean isDataGeneration() {
        return FMLLoader.getLaunchHandler().isData();
    }

    @Override
    public Map<String, ModContainer> getModList() {
        return this.modList.get();
    }

    @Override
    public ObjectShareAccess getObjectShareAccess() {
        return EmptyObjectShareAccessImpl.INSTANCE;
    }
}