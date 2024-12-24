package fuzs.puzzleslib.impl.config;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class ConfigHolderImpl implements ConfigHolder.Builder {
    private final String modId;
    private Map<Class<?>, ConfigDataHolderImpl<?>> configsByClass = new IdentityHashMap<>();

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
        Objects.requireNonNull(holder, "No config holder available for type " + clazz);
        return (ConfigDataHolder<T>) holder;
    }

    @Override
    public <T extends ConfigCore> Builder client(Class<T> clazz) {
        // this is necessary to allow safely using client-only classes in the client configs (e.g. certain enums for vanilla game options)
        Supplier<T> supplier = ModLoaderEnvironment.INSTANCE.isClient() ? construct(clazz) : () -> null;
        if (this.configsByClass.put(clazz, this.client(supplier)) != null) {
            throw new IllegalStateException("Duplicate registration for client config of type " + clazz);
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder common(Class<T> clazz) {
        if (this.configsByClass.put(clazz, this.common(construct(clazz))) != null) {
            throw new IllegalStateException("Duplicate registration for common config of type " + clazz);
        }
        return this;
    }

    @Override
    public <T extends ConfigCore> Builder server(Class<T> clazz) {
        if (this.configsByClass.put(clazz, this.server(construct(clazz))) != null) {
            throw new IllegalStateException("Duplicate registration for server config of type " + clazz);
        }
        return this;
    }

    protected abstract <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier);

    protected abstract <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier);

    protected abstract <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier);

    @Override
    public <T extends ConfigCore> Builder setFileName(Class<T> clazz, UnaryOperator<String> fileNameFactory) {
        ((ConfigDataHolderImpl<T>) this.getHolder(clazz)).setFileNameFactory(fileNameFactory);
        return this;
    }

    @Override
    public final void build() {
        this.configsByClass = ImmutableMap.copyOf(this.configsByClass);
        // register events before registering configs
        for (ConfigDataHolderImpl<?> holder : this.configsByClass.values()) {
            // this is the wrong physical side for this config, it hasn't been loaded and doesn't need any processing
            if (holder.config != null) this.bake(holder, this.modId);
        }
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            this.registerConfigurationScreen(this.modId);
        }
    }

    protected abstract void bake(ConfigDataHolderImpl<?> holder, String modId);

    @MustBeInvokedByOverriders
    protected void registerConfigurationScreen(String modId) {
        ClientAbstractions.INSTANCE.registerConfigScreenFactory(modId);
    }
}
