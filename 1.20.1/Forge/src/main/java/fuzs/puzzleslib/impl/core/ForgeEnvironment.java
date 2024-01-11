package fuzs.puzzleslib.impl.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ForgeEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        List<? extends IModInfo> modList;
        if (ModList.get() != null) {
            modList = ModList.get().getMods();
        } else if (FMLLoader.getLoadingModList() != null) {
            modList = FMLLoader.getLoadingModList().getMods();
        } else {
            throw new NullPointerException("mod list is null");
        }
        return modList.stream()
                .map(ForgeModContainer::new)
                .sorted(Comparator.comparing(ModContainer::getModId))
                // compiler cannot infer type arguments here
                .collect(ImmutableMap.<ForgeModContainer, String, ModContainer>toImmutableMap(ModContainer::getModId, Function.identity()));
    });

    @Override
    public ModLoader getModLoader() {
        return ModLoader.FORGE;
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
    public Map<String, ModContainer> getModList() {
        return this.modList.get();
    }

    @Override
    public ObjectShareAccess getObjectShareAccess() {
        return ForgeObjectShareAccess.INSTANCE;
    }
}