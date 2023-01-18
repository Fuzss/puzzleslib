package fuzs.puzzleslib.config.core;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.function.Function;

public class FabricConfigFileTypeHandler extends ConfigFileTypeHandler {
    static final ConfigFileTypeHandler INSTANCE = new FabricConfigFileTypeHandler();

    @Override
    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return super.reader(ModLoaderEnvironment.INSTANCE.getConfigDir());
    }

    @Override
    public void unload(Path configBasePath, ModConfig config) {
        super.unload(ModLoaderEnvironment.INSTANCE.getConfigDir(), config);
    }
}
