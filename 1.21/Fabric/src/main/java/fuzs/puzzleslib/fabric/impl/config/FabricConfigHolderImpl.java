package fuzs.puzzleslib.fabric.impl.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigTranslationsManager;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

import java.util.function.Supplier;

public class FabricConfigHolderImpl extends ConfigHolderImpl {

    public FabricConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(this.modId, ModConfig.Type.CLIENT, ModConfig.Type.CLIENT, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(this.modId, ModConfig.Type.COMMON, ModConfig.Type.COMMON, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(this.modId, ModConfig.Type.SERVER, supplier);
    }

    @Override
    public void build() {
        super.build();
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ConfigScreenFactoryRegistry.INSTANCE.register(this.modId, ConfigurationScreen::new);
        }
    }

    @Override
    protected void bake(ConfigDataHolderImpl<?> holder) {
        NeoForgeModConfigEvents.loading(this.modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.LOADING
            );
        });
        NeoForgeModConfigEvents.reloading(this.modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.RELOADING
            );
        });
        NeoForgeModConfigEvents.unloading(this.modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.UNLOADING
            );
        });
        ((FabricConfigDataHolderImpl<?>) holder).register();
    }

    private static class FabricConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;

        FabricConfigDataHolderImpl(String modId, ModConfig.Type configType, Supplier<T> supplier) {
            this(modId, configType, configType, supplier);
        }

        FabricConfigDataHolderImpl(String modId, ModConfig.Type configType, ModConfig.Type configNameType, Supplier<T> supplier) {
            super(modId, supplier);
            this.setFileNameFactory(ConfigHolder.getDefaultNameFactory(configNameType.extension()));
            this.configType = configType;
        }

        void onModConfig(ModConfig modConfig, ModConfigEventType eventType) {
            if (modConfig.getType() == this.configType) {
                super.onModConfig(modConfig.getFileName(), eventType);
            }
        }

        void register() {
            this.initializeFileName();
            NeoForgeConfigRegistry.INSTANCE.register(this.getModId(), this.configType, this.buildSpec(), this.getFileName());
            ConfigTranslationsManager.addConfig(this.getModId(), this.getFileName(), this.configType.extension());
        }
    }
}
