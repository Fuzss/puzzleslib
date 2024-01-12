package fuzs.puzzleslib.fabric.impl.config;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config, true, "Loading");
        });
        NeoForgeModConfigEvents.reloading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config, true, "Reloading");
        });
        NeoForgeModConfigEvents.unloading(modId).register((ModConfig config) -> {
            ((FabricConfigDataHolderImpl<?>) holder).onModConfig(config, false, "Unloading");
        });
        ((FabricConfigDataHolderImpl<?>) holder).register(modId);
    }

    private static class FabricConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
        private final ModConfig.Type configType;
        @Nullable
        private ModConfig modConfig;

        protected FabricConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
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
            this.modConfig = new ModConfig(this.configType, this.buildSpec(), modId, this.fileName.apply(modId));
        }
    }
}
