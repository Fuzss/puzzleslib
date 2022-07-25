package fuzs.puzzleslib.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.core.DistType;
import fuzs.puzzleslib.core.DistTypeExecutor;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * config holder implementation on Fabric (builder is only used initially, just store this instance as {@link ConfigHolderV2})
 * only really different from Forge in {@link #bakeConfigs}
 */
public class FabricConfigHolderImplV2 implements ConfigHolderV2.Builder {
    /**
     * all configs in this holder, made immutable on baking
     */
    private Map<Class<? extends AbstractConfig>, FabricConfigDataHolderImplV2<? extends AbstractConfig>> configsByClass = Maps.newIdentityHashMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractConfig> ConfigDataHolderV2<T> getHolder(Class<T> clazz) {
        FabricConfigDataHolderImplV2<? extends AbstractConfig> holder = this.configsByClass.get(clazz);
        Objects.requireNonNull(holder, String.format("No config holder available for type %s", clazz));
        return (ConfigDataHolderV2<T>) holder;
    }

    @Override
    public <T extends AbstractConfig> Builder clientConfig(Class<T> clazz, Supplier<T> clientConfig) {
        // this is necessary to allow safely using client-only classes in the client configs (e.g. certain enums for vanilla game options)
        Supplier<T> config = () -> DistTypeExecutor.getWhenOn(DistType.CLIENT, () -> clientConfig);
        if (this.configsByClass.put(clazz, new FabricConfigDataHolderImplV2<T>(ModConfig.Type.CLIENT, config)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for client config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends AbstractConfig> Builder commonConfig(Class<T> clazz, Supplier<T> commonConfig) {
        if (this.configsByClass.put(clazz, new FabricConfigDataHolderImplV2<T>(ModConfig.Type.COMMON, commonConfig)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for common config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends AbstractConfig> Builder serverConfig(Class<T> clazz, Supplier<T> serverConfig) {
        if (this.configsByClass.put(clazz, new FabricConfigDataHolderImplV2<>(ModConfig.Type.SERVER, serverConfig)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for server config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends AbstractConfig> Builder setFileName(Class<T> clazz, UnaryOperator<String> fileName) {
        FabricConfigDataHolderImplV2<T> holder = (FabricConfigDataHolderImplV2<T>) this.getHolder(clazz);
        holder.setFileName(fileName);
        return this;
    }

    @Override
    public void bakeConfigs(String modId) {
        this.configsByClass = ImmutableMap.copyOf(this.configsByClass);
        // register events before registering configs
        for (FabricConfigDataHolderImplV2<? extends AbstractConfig> holder : this.configsByClass.values()) {
            // this is fired on ModEventBus, so mod id check is not necessary here
            // we keep this as it's required on Fabric though due to a dedicated ModEventBus being absent
            ModConfigEvent.LOADING.register((ModConfig config) -> {
                if (config.getModId().equals(modId)) {
                    holder.onModConfig(config, false);
                }
            });
            ModConfigEvent.RELOADING.register((ModConfig config) -> {
                if (config.getModId().equals(modId)) {
                    holder.onModConfig(config, true);
                }
            });
            holder.register((ModConfig.Type type, ForgeConfigSpec spec, UnaryOperator<String> fileName) -> {
                return ModLoadingContext.registerConfig(modId, type, spec, fileName.apply(modId));
            });
        }
    }
}
