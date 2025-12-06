package fuzs.puzzleslib.neoforge.impl.config;

import com.electronwill.nightconfig.core.file.FileWatcher;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.nio.file.Path;
import java.util.function.Supplier;

public class NeoForgeConfigHolderImpl extends ConfigHolderImpl {

    public NeoForgeConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(ModConfig.Type.STARTUP, ModConfig.Type.CLIENT, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(ModConfig.Type.STARTUP, ModConfig.Type.COMMON, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(ModConfig.Type.SERVER, supplier);
    }

    @Override
    protected void bake(ConfigDataHolderImpl<?> holder, String modId) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            this.registerLoadingHandlers(eventBus, holder);
        });
        ((NeoForgeConfigDataHolderImpl<?>) holder).register(modId);
    }

    private void registerLoadingHandlers(IEventBus eventBus, ConfigDataHolderImpl<?> holder) {
        eventBus.addListener((final ModConfigEvent.Loading event) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(event.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.LOADING);
        });
        eventBus.addListener((final ModConfigEvent.Reloading event) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(event.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.RELOADING);
        });
        eventBus.addListener((final ModConfigEvent.Unloading event) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(event.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.UNLOADING);
        });
    }

    private static class NeoForgeConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;

        NeoForgeConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
            this(configType, configType, supplier);
        }

        NeoForgeConfigDataHolderImpl(ModConfig.Type configType, ModConfig.Type configNameType, Supplier<T> supplier) {
            super(supplier);
            this.setFileNameFactory(ConfigHolder.getDefaultNameFactory(configNameType.extension()));
            this.configType = configType;
        }

        void onModConfig(ModConfig modConfig, ModConfigEventType eventType) {
            if (modConfig.getType() == this.configType) {
                super.onModConfig(eventType, modConfig.getFileName(), () -> {
                    if (modConfig.getLoadedConfig() != null && !modConfig.getLoadedConfig()
                            .config()
                            .configFormat()
                            .isInMemory()) {
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

        void register(String modId) {
            ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(modId);
            modContainer.registerConfig(this.configType, this.initialize(modId), this.getFileName());
        }
    }
}
