package fuzs.puzzleslib.neoforge.impl.config;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigTranslationsManager;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class NeoForgeConfigHolderImpl extends ConfigHolderImpl {

    public NeoForgeConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(this.modId, ModConfig.Type.CLIENT, ModConfig.Type.CLIENT, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(this.modId, ModConfig.Type.COMMON, ModConfig.Type.COMMON, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(this.modId, ModConfig.Type.SERVER, supplier);
    }

    @Override
    public void build() {
        super.build();
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(this.modId);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    @Override
    protected void bake(ConfigDataHolderImpl<?> holder) {
        Optional<IEventBus> optional = NeoForgeModContainerHelper.getOptionalModEventBus(this.modId);
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Loading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.LOADING
            );
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Reloading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.RELOADING
            );
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Unloading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(),
                    ConfigDataHolderImpl.ModConfigEventType.UNLOADING
            );
        }));
        ((NeoForgeConfigDataHolderImpl<?>) holder).register();
    }

    private static class NeoForgeConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;

        NeoForgeConfigDataHolderImpl(String modId, ModConfig.Type configType, Supplier<T> supplier) {
            this(modId, configType, configType, supplier);
        }

        NeoForgeConfigDataHolderImpl(String modId, ModConfig.Type type, ModConfig.Type configNameType, Supplier<T> supplier) {
            super(modId, supplier);
            this.setFileNameFactory(ConfigHolder.getDefaultNameFactory(configNameType.extension()));
            this.configType = type;
        }

        void onModConfig(ModConfig modConfig, ModConfigEventType eventType) {
            if (modConfig.getType() == this.configType) {
                super.onModConfig(modConfig.getFileName(), eventType);
            }
        }

        void register() {
            this.initializeFileName();
            ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(this.getModId());
            modContainer.registerConfig(this.configType, this.buildSpec(), this.getFileName());
            ConfigTranslationsManager.addConfig(this.getModId(), this.getFileName(), this.configType.extension());
        }
    }
}
