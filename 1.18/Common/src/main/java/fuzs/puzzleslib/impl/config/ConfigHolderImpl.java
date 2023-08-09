package fuzs.puzzleslib.impl.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.DistType;
import fuzs.puzzleslib.api.core.v1.DistTypeExecutor;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class ConfigHolderImpl implements ConfigHolder.Builder {
    private final String modId;
    private Map<Class<?>, ConfigDataHolderImpl<?>> configsByClass = Maps.newIdentityHashMap();

    protected ConfigHolderImpl(String modId) {
        this.modId = modId;
    }

    @SuppressWarnings("unchecked")
    private static <T extends ConfigCore> Supplier<T> construct(Class<T> clazz) {
        return () -> {
            try {
                return (T) MethodHandles.publicLookup()
                        .findConstructor(clazz, MethodType.methodType(void.class))
                        .invoke();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> clazz) {
        ConfigDataHolderImpl<?> holder = this.configsByClass.get(clazz);
        Objects.requireNonNull(holder, String.format("No config holder available for type %s", clazz));
        return (ConfigDataHolder<T>) holder;
    }

    @Override
    public <T extends ConfigCore> Builder client(Class<T> clazz) {
        // this is necessary to allow safely using client-only classes in the client configs (e.g. certain enums for vanilla game options)
        Supplier<T> config = () -> DistTypeExecutor.getWhenOn(DistType.CLIENT, () -> construct(clazz));
        if (this.configsByClass.put(clazz, new ConfigDataHolderImpl<>(ModConfig.Type.CLIENT, config)) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for client config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder common(Class<T> clazz) {
        if (this.configsByClass.put(clazz, new ConfigDataHolderImpl<>(ModConfig.Type.COMMON, construct(clazz))) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for common config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder server(Class<T> clazz) {
        if (this.configsByClass.put(clazz, new ConfigDataHolderImpl<>(ModConfig.Type.SERVER, construct(clazz))) != null) {
            throw new IllegalStateException(String.format("Duplicate registration for server config of type %s", clazz));
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder setFileName(Class<T> clazz, UnaryOperator<String> fileName) {
        ConfigDataHolderImpl<T> holder = (ConfigDataHolderImpl<T>) this.getHolder(clazz);
        holder.fileName = fileName;
        return this;
    }

    @Override
    public void build() {
        this.configsByClass = ImmutableMap.copyOf(this.configsByClass);
        // register events before registering configs
        for (ConfigDataHolderImpl<?> holder : this.configsByClass.values()) {
            // this is the wrong physical side for this config, it hasn't been loaded and doesn't need any processing
            if (holder.config != null) this.bake(holder, this.modId);
        }
    }

    abstract void bake(ConfigDataHolderImpl<?> holder, String modId);
}
