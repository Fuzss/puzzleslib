package fuzs.puzzleslib.forge.impl.core;

import com.google.common.base.Suppliers;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import fuzs.puzzleslib.impl.core.EmptyObjectShareAccessImpl;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ForgeEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        return ModContainer.toModList(this::getForgeModContainers);
    });

    private Stream<? extends ModContainer> getForgeModContainers() {
        Map<String, ForgeModContainer> allMods = getForgeModList().stream()
                .map(ForgeModContainer::new)
                .collect(Collectors.toMap(modContainer -> {
                    // non-raw variant provides an unescaped uri
                    // raw variant is escaped once ('space' is replaced with %20)
                    return modContainer.getURI().getRawSchemeSpecificPart();
                }, Function.identity(), (ForgeModContainer o1, ForgeModContainer o2) -> {
                    o2.setParent(o1);
                    return o1;
                }));
        for (ForgeModContainer modContainer : allMods.values()) {
            if (modContainer.getURI().getScheme().equals("union")) {
                // raw variant provides an uri which has been escaped twice (%20, originally a 'space', is replaced with %2520)
                // non-raw variant is escaped once ('space' is replaced with %20)
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
        // jar-in-jar mods can also be put outside META-INF, but this is the default place for Forge Gradle & Architectury Loom
        return schemePart.replace("/jij:file:///", "file:///")
                .replaceAll("_/META-INF/.+(#|%23)\\d+!/$", "!/");
    }

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
    public String getCurrentMappingsNamespace() {
        return FMLEnvironment.naming.equalsIgnoreCase("mcp") ? "named" : FMLEnvironment.naming;
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