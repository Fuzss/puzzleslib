package fuzs.puzzleslib.fabric.impl.config;

import com.electronwill.nightconfig.core.file.FileWatcher;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;
import java.util.function.Supplier;

public class FabricConfigHolderImpl extends ConfigHolderImpl {

    public FabricConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(ModConfig.Type.CLIENT, ModConfig.Type.CLIENT, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(ModConfig.Type.COMMON, ModConfig.Type.COMMON, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(ModConfig.Type.SERVER, supplier);
    }

    @Override
    protected void bake(ConfigDataHolderImpl<?> holder, String modId) {
        NeoForgeModConfigEvents.loading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.LOADING);
        });
        NeoForgeModConfigEvents.reloading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.RELOADING);
        });
        NeoForgeModConfigEvents.unloading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.UNLOADING);
        });
        ((FabricConfigDataHolderImpl<?>) holder).register(modId);
    }

    private static class FabricConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;

        FabricConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
            this(configType, configType, supplier);
        }

        FabricConfigDataHolderImpl(ModConfig.Type configType, ModConfig.Type configNameType, Supplier<T> supplier) {
            super(supplier);
            this.setFileNameFactory(ConfigHolder.getDefaultNameFactory(configNameType.extension()));
            this.configType = configType;
        }

        void onModConfig(ModConfig modConfig, ModConfigEventType eventType) {
            if (modConfig.getType() == this.configType) {
                super.onModConfig(eventType, modConfig.getFileName(), () -> {
                    if (modConfig.getLoadedConfig() != null &&
                            !modConfig.getLoadedConfig().config().configFormat().isInMemory()) {
                        try {
                            Path path = modConfig.getFullPath();
                            FileWatcher.defaultInstance().removeWatch(path);
                        } catch (RuntimeException exception) {
                            PuzzlesLib.LOGGER.error("Failed to remove config {} from tracker!",
                                    modConfig.getFileName(),
                                    exception);
                        }
                    }
                });
            }
        }

        @Override
        protected ModConfigSpec register(String modId) {
            ModConfigSpec modConfigSpec = super.register(modId);
            NeoForgeConfigRegistry.INSTANCE.register(modId, this.configType, modConfigSpec, this.getFileName());
            return modConfigSpec;
        }
    }
}
