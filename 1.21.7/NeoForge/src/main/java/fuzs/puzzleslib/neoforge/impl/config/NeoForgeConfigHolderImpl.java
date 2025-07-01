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
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;
import java.util.Optional;
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
        Optional<IEventBus> optional = NeoForgeModContainerHelper.getOptionalModEventBus(modId);
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Loading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.LOADING);
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Reloading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.RELOADING);
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Unloading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.UNLOADING);
        }));
        ((NeoForgeConfigDataHolderImpl<?>) holder).register(modId);
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
            ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(modId);
            modContainer.registerConfig(this.configType, modConfigSpec, this.getFileName());
            return modConfigSpec;
        }
    }
}
