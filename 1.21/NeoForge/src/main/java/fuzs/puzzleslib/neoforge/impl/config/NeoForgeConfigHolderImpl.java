package fuzs.puzzleslib.neoforge.impl.config;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class NeoForgeConfigHolderImpl extends ConfigHolderImpl {

    public NeoForgeConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(ModConfig.Type.STARTUP, "client", supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(ModConfig.Type.STARTUP, "common", supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
        return new NeoForgeConfigDataHolderImpl<>(ModConfig.Type.SERVER, supplier);
    }

    @Override
    protected void bake(ConfigDataHolderImpl<?> holder, String modId) {
        Optional<IEventBus> optional = NeoForgeModContainerHelper.getOptionalModEventBus(modId);
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Loading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(), true, "Loading");
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Reloading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(), true, "Reloading");
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Unloading evt) -> {
            ((NeoForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(), false, "Unloading");
        }));
        ((NeoForgeConfigDataHolderImpl<?>) holder).register(modId);
    }

    private static class NeoForgeConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;
        @Nullable
        private ModConfig modConfig;

        protected NeoForgeConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
            this(configType, configType.extension(), supplier);
        }

        protected NeoForgeConfigDataHolderImpl(ModConfig.Type type, String configTypeName, Supplier<T> supplier) {
            super(configTypeName, supplier);
            this.configType = type;
        }

        @Override
        protected Either<Unit, String> findErrorMessage() {
            if (this.modConfig == null) {
                return Either.right("Mod config instance is missing");
            } else if (this.modConfig.getConfigData() == null) {
                return Either.right("Config data is missing");
            } else {
                return super.findErrorMessage();
            }
        }

        public void onModConfig(ModConfig modConfig, boolean isLoading, String eventType) {
            // null config must be permitted for config loading as the event is triggered during construction of ModConfig (before the field can even be set)
            if (modConfig.getType() == this.configType && (this.modConfig == null || modConfig == this.modConfig)) {
                super.onModConfig(modConfig.getModId(), isLoading, eventType);
            }
        }

        public void register(String modId) {
            Objects.requireNonNull(this.config, "Attempting to register invalid config of type %s".formatted(this.configTypeName));
            if (this.modConfig != null) throw new IllegalStateException(String.format("Config for type %s has already been registered", this.configTypeName));
            ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(modId);
            this.modConfig = new ModConfig(this.configType, this.buildSpec(), modContainer, this.fileName.apply(modId));
            modContainer.addConfig(this.modConfig);
        }
    }
}
