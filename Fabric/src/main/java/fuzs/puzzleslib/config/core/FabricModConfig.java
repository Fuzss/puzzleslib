package fuzs.puzzleslib.config.core;

import net.fabricmc.loader.api.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * A custom implementation of {@link ModConfig} that places server config files in the global config directory at <code>.minecraft/config</code> instead of storing them locally per world.
 * All main advantages of server configs like server to client syncing are kept intact, only locally applying a server config to a single world is no longer possible.
 * This change is made as there is no real advantage to having local per-world server configs, and most of all this circumstance just leads to a lot of user confusion and frustration.
 *
 * <p>This idea is taken from the Corail Woodcutter mod, found here: <a href="https://www.curseforge.com/minecraft/mc-mods/corail-woodcutter">Corail Woodcutter</a>
 */
public class FabricModConfig extends ModConfig {
    private final ConfigFileTypeHandler configHandler;

    public FabricModConfig(Type type, IConfigSpec<?> spec, ModContainer container, String fileName) {
        super(type, spec, container, fileName);
        this.configHandler = FabricConfigFileTypeHandler.TOML;
    }

    public FabricModConfig(Type type, IConfigSpec<?> spec, ModContainer activeContainer) {
        super(type, spec, activeContainer);
        this.configHandler = FabricConfigFileTypeHandler.TOML;
    }

    @Override
    public ConfigFileTypeHandler getHandler() {
        return this.configHandler;
    }
}
