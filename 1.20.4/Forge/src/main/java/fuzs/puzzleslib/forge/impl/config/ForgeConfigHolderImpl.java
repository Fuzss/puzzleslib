package fuzs.puzzleslib.forge.impl.config;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigSpecAdapter;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.forge.api.core.v1.ForgeModContainerHelper;
import fuzs.puzzleslib.forge.impl.config.core.ForgeModConfig;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class ForgeConfigHolderImpl extends ConfigHolderImpl {

    public ForgeConfigHolderImpl(String modId) {
        super(modId);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
        return new ForgeConfigDataHolderImpl<>(ModConfig.Type.CLIENT, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
        return new ForgeConfigDataHolderImpl<>(ModConfig.Type.COMMON, supplier);
    }

    @Override
    protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
        return new ForgeConfigDataHolderImpl<>(ModConfig.Type.SERVER, supplier);
    }

    @Override
    protected void bake(ConfigDataHolderImpl<?> holder, String modId) {
        Optional<IEventBus> optional = ForgeModContainerHelper.getOptionalModEventBus(modId);
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Loading evt) -> {
            ((ForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(), true, "Loading");
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Reloading evt) -> {
            ((ForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(), true, "Reloading");
        }));
        optional.ifPresent(eventBus -> eventBus.addListener((final ModConfigEvent.Unloading evt) -> {
            ((ForgeConfigDataHolderImpl<?>) holder).onModConfig(evt.getConfig(), false, "Unloading");
        }));
        ((ForgeConfigDataHolderImpl<?>) holder).register(modId);
    }

    private static class ForgeConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;
        @Nullable
        private ModConfig modConfig;

        protected ForgeConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
            super(configType.extension(), supplier);
            this.configType = configType;
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
            if (modConfig.getType() == this.configType && (this.modConfig == null || modConfig == this.modConfig)) {
                super.onModConfig(modConfig.getModId(), isLoading, eventType);
            }
        }

        public void register(String modId) {
            Objects.requireNonNull(this.config, "Attempting to register invalid config of type %s".formatted(this.configType.extension()));
            if (this.modConfig != null) throw new IllegalStateException(String.format("Config for type %s has already been registered!", this.configType.extension()));
            ModContainer modContainer = ForgeModContainerHelper.getModContainer(modId);
            this.modConfig = new ForgeModConfig(this.configType, new NeoForgeConfigSpecAdapter(this.buildSpec()), modContainer, this.fileName.apply(modId));
            modContainer.addConfig(this.modConfig);
        }
    }
}
