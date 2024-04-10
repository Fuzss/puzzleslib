package fuzs.puzzleslib.impl.core;

import com.google.common.base.Suppliers;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FabricEnvironment implements ModLoaderEnvironment {
    private final Supplier<Map<String, ModContainer>> modList = Suppliers.memoize(() -> {
        return ModContainer.toModList(this::getFabricModContainers);
    });

    private Stream<? extends ModContainer> getFabricModContainers() {
        Map<net.fabricmc.loader.api.ModContainer, FabricModContainer> allMods = FabricLoader.getInstance()
                .getAllMods()
                .stream()
                .map(FabricModContainer::new)
                .collect(Collectors.toMap(FabricModContainer::getFabricModContainer,
                        Function.identity(),
                        (FabricModContainer o1, FabricModContainer o2) -> {
                            o2.setParent(o1);
                            return o1;
                        }
                ));
        for (FabricModContainer modContainer : allMods.values()) {
            modContainer.getFabricModContainer()
                    .getContainedMods()
                    .stream()
                    .map(allMods::get)
                    .forEach(childModContainer -> {
                        childModContainer.setParent(modContainer);
                    });
        }
        return allMods.values().stream();
    }

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
        return FabricLoader.getInstance().isDevelopmentEnvironment();
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