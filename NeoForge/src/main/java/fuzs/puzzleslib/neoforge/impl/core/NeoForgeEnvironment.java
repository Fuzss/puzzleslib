package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.base.Suppliers;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class NeoForgeEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        return ModContainer.toModList(this.getNeoForgeModContainers());
    });

    private Stream<? extends ModContainer> getNeoForgeModContainers() {
        Map<String, NeoForgeModContainer> allMods = getForgeModList().stream()
                .map(NeoForgeModContainer::new)
                .collect(Collectors.toMap(modContainer -> {
                    // alternatively use raw variant for escaped octets
                    return modContainer.getURI().getSchemeSpecificPart();
                }, Function.identity()));
        for (NeoForgeModContainer modContainer : allMods.values()) {
            if (modContainer.getURI().getScheme().equals("union")) {
                // alternatively use raw variant for escaped octets
                String schemePart = getParentSchemePart(modContainer.getURI().getSchemeSpecificPart());
                modContainer.setParent(allMods.get(schemePart));
            }
        }
        return allMods.values().stream();
    }

    private static List<? extends IModInfo> getForgeModList() {
        if (ModList.get() != null) {
            return ModList.get().getMods();
        } else if (FMLLoader.getLoadingModList() != null) {
            return FMLLoader.getLoadingModList().getMods();
        } else {
            throw new NullPointerException("mod list is null");
        }
    }

    private static String getParentSchemePart(String schemePart) {
        // jar-in-jar mods can also be put outside META-INF, but this is the default place for NeoGradle & Architectury Loom
        return schemePart.replace("/jij:file:///", "file:///")
                .replaceAll("_/META-INF/.+(#|%23)\\d+!/$", "!/");
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
    public String getCurrentMappingsNamespace() {
        return "named";
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