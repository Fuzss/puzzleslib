package fuzs.puzzleslib.fabric.impl.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import net.neoforged.fml.config.ModConfig;

import java.util.function.Supplier;

public class FabricConfigHolderImpl extends ConfigHolderImpl {

    public FabricConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(ModConfig.Type.CLIENT, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new FabricConfigDataHolderImpl<>(ModConfig.Type.COMMON, supplier);
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

    private static class FabricConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;

        protected FabricConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
            this(configType, configType, supplier);
        }

        protected FabricConfigDataHolderImpl(ModConfig.Type type, ModConfig.Type configNameType, Supplier<T> supplier) {
            super(configNameType.extension(), supplier);
            this.configType = type;
        }

        void onModConfig(ModConfig modConfig, ModConfigEventType eventType) {
            if (modConfig.getType() == this.configType) {
                super.onModConfig(modConfig.getFileName(), eventType);
            }
        }

        @Override
        protected void register(String modId) {
            super.register(modId);
            NeoForgeConfigRegistry.INSTANCE.register(modId, this.configType, this.buildSpec(), this.getFileName());
        }
    }
}
