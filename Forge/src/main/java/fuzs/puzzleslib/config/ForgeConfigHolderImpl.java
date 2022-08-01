package fuzs.puzzleslib.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.PuzzlesLibForge;
import fuzs.puzzleslib.core.DistType;
import fuzs.puzzleslib.core.DistTypeExecutor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * config holder implementation on Forge (builder is only used initially, just store this instance as {@link ConfigHolder})
 * only really different from Fabric in {@link #bakeConfigs}
 */
public class ForgeConfigHolderImpl implements ConfigHolder.Builder {
    /**
     * all configs in this holder, made immutable on baking
     */
    private Map<Class<? extends ConfigCore>, ForgeConfigDataHolderImpl<? extends ConfigCore>> configsByClass = Maps.newIdentityHashMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> clazz) {
        ForgeConfigDataHolderImpl<? extends ConfigCore> holder = this.configsByClass.get(clazz);
        Objects.requireNonNull(holder, String.format("No config holder available for type %s", clazz));
        return (ConfigDataHolder<T>) holder;
    }

    @Override
    public <T extends ConfigCore> Builder clientConfig(Class<T> clazz, Supplier<T> clientConfig) {
        // this is necessary to allow safely using client-only classes in the client configs (e.g. certain enums for vanilla game options)
        Supplier<T> config = () -> DistTypeExecutor.getWhenOn(DistType.CLIENT, () -> clientConfig);
        if (this.configsByClass.put(clazz, new ForgeConfigDataHolderImpl<T>(ModConfig.Type.CLIENT, config)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for client config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder commonConfig(Class<T> clazz, Supplier<T> commonConfig) {
        if (this.configsByClass.put(clazz, new ForgeConfigDataHolderImpl<T>(ModConfig.Type.COMMON, commonConfig)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for common config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder serverConfig(Class<T> clazz, Supplier<T> serverConfig) {
        if (this.configsByClass.put(clazz, new ForgeConfigDataHolderImpl<>(ModConfig.Type.SERVER, serverConfig)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for server config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder setFileName(Class<T> clazz, UnaryOperator<String> fileName) {
        ForgeConfigDataHolderImpl<T> holder = (ForgeConfigDataHolderImpl<T>) this.getHolder(clazz);
        holder.setFileName(fileName);
        return this;
    }

    @Override
    public void bakeConfigs(String modId) {
        this.configsByClass = ImmutableMap.copyOf(this.configsByClass);
        // register events before registering configs
        final IEventBus modBus = PuzzlesLibForge.findModEventBus(modId);
        for (ForgeConfigDataHolderImpl<? extends ConfigCore> holder : this.configsByClass.values()) {
            // this is the wrong physical side for this config, it hasn't been loaded and doesn't need any processing
            if (holder.config == null) continue;
            // this is fired on ModEventBus, so mod id check is not necessary here
            // we keep this as it's required on Fabric though due to a dedicated ModEventBus being absent
            modBus.addListener((final ModConfigEvent.Loading evt) -> {
                if (evt.getConfig().getModId().equals(modId)) {
                    holder.onModConfig(evt.getConfig(), false);
                }
            });
            modBus.addListener((final ModConfigEvent.Reloading evt) -> {
                if (evt.getConfig().getModId().equals(modId)) {
                    holder.onModConfig(evt.getConfig(), true);
                }
            });
            holder.register((ModConfig.Type type, ForgeConfigSpec spec, UnaryOperator<String> fileName) -> {
                ModContainer modContainer = PuzzlesLibForge.findModContainer(modId);
                ModConfig modConfig = new ModConfig(type, spec, modContainer, fileName.apply(modId));
                modContainer.addConfig(modConfig);
                return modConfig;
            });
        }
    }
}
