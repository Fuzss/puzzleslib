package fuzs.puzzleslib.neoforge.impl.config;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

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
        ((NeoForgeConfigDataHolderImpl<?>) holder).register(modId);
    }

    private static class NeoForgeConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;

        protected NeoForgeConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
            this(configType, configType, supplier);
        }

        protected NeoForgeConfigDataHolderImpl(ModConfig.Type type, ModConfig.Type configNameType, Supplier<T> supplier) {
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
            ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(modId);
            modContainer.registerConfig(this.configType, this.buildSpec(), this.getFileName());
        }
    }
}
