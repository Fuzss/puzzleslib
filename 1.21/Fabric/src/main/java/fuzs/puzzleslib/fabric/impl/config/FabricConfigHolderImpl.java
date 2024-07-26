package fuzs.puzzleslib.fabric.impl.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

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
                    ConfigDataHolderImpl.ModConfigEventType.LOADING
            );
        });
        NeoForgeModConfigEvents.reloading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.RELOADING
            );
        });
        NeoForgeModConfigEvents.unloading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config,
                    ConfigDataHolderImpl.ModConfigEventType.UNLOADING
            );
        });
        ((FabricConfigDataHolderImpl<?>) holder).register(modId);
    }

    @Override
    public void registerConfigurationScreen(String modId) {
        ConfigScreenFactoryRegistry.INSTANCE.register(modId, ConfigurationScreen::new);
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
                super.onModConfig(modConfig.getFileName(), eventType);
            }
        }

        void register(String modId) {
            ModConfigSpec configSpec = this.setupConfigSpec(modId, this.configType.extension());
            NeoForgeConfigRegistry.INSTANCE.register(modId, this.configType, configSpec, this.getFileName());
        }
    }
}
