package fuzs.puzzleslib.impl.config;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.config.annotation.ConfigBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ConfigDataHolderImpl<T extends ConfigCore> implements ConfigDataHolder<T>, ValueCallback {
    static final int MAX_CONSECUTIVE_CONFIG_RELOADS = 25;

    final T config;
    private final Supplier<T> defaultConfigSupplier;
    private final List<Consumer<T>> additionalCallbacks = new ArrayList<>();
    @Nullable
    private T defaultConfig;
    private UnaryOperator<String> fileNameFactory;
    @Nullable
    private String fileName;
    private List<Runnable> configValueCallbacks = new ArrayList<>();
    private ModConfigStatus status = ModConfigStatus.CONFIG_MISSING;
    private int consecutiveConfigReloads;

    protected ConfigDataHolderImpl(Supplier<T> supplier) {
        this.config = supplier.get();
        this.defaultConfigSupplier = supplier;
    }

    @Override
    public T getConfig() {
        Objects.requireNonNull(this.config, "config is null");
        return this.isAvailable() ? this.config : this.getOrCreateDefaultConfig();
    }

    private T getOrCreateDefaultConfig() {
        if (this.defaultConfig == null) {
            this.status.throwIfNecessary(this.config);
            this.defaultConfig = this.defaultConfigSupplier.get();
            Objects.requireNonNull(this.defaultConfig, "default config is null");
            this.defaultConfig.afterConfigReload();
            for (Consumer<T> callback : this.additionalCallbacks) {
                callback.accept(this.defaultConfig);
            }
        }
        return this.defaultConfig;
    }

    @Override
    public boolean isAvailable() {
        return this.config != null && this.status == ModConfigStatus.FULLY_LOADED;
    }

    @Override
    public void addCallback(Consumer<T> callback) {
        this.additionalCallbacks.add(callback);
    }

    @Override
    public <S, V extends ModConfigSpec.ConfigValue<S>> V accept(V entry, Consumer<S> save) {
        Objects.requireNonNull(entry, "entry is null");
        this.acceptValueCallback(() -> save.accept(entry.get()));
        return entry;
    }

    public void acceptValueCallback(Runnable runnable) {
        this.configValueCallbacks.add(runnable);
    }

    protected void onModConfig(ModConfigEventType eventType, String fileName, Runnable removeWatch) {
        if (Objects.equals(fileName, this.getFileName())) {
            PuzzlesLib.LOGGER.info("Dispatching {} event for config {}", eventType, fileName);
            Objects.requireNonNull(this.config, () -> "config is null: " + fileName);
            if (eventType.isLoading()) {
                this.configValueCallbacks.forEach(Runnable::run);
                // set this only after callbacks have run, to ensure nothing is null any more when the config reports as available in case of some concurrency issues
                this.status = ModConfigStatus.FULLY_LOADED;
                for (Consumer<T> callback : this.additionalCallbacks) {
                    callback.accept(this.config);
                }
            } else {
                this.status = ModConfigStatus.DATA_MISSING;
            }
            if (eventType != ModConfigEventType.RELOADING) {
                this.consecutiveConfigReloads = 0;
            } else if (++this.consecutiveConfigReloads >= MAX_CONSECUTIVE_CONFIG_RELOADS) {
                // remove file watcher for configs that reload too often consecutively,
                // this is sometimes caused by issues with the file system and pointlessly creates new config files repeatedly
                // this is also triggered after editing the config in-game, but if the user is doing that, they are unlikely to also edit the file in the same session
                removeWatch.run();
            }
        }
    }

    protected final ModConfigSpec initialize(String modId) {
        Objects.requireNonNull(this.config, "Attempting to register invalid config for " + modId);
        if (this.fileName == null || this.status != ModConfigStatus.CONFIG_MISSING) {
            throw new IllegalStateException(
                    "Config has already been registered at " + this.fileNameFactory.apply(modId));
        }
        this.fileName = this.fileNameFactory.apply(modId);
        this.status = ModConfigStatus.DATA_MISSING;
        return this.buildConfigSpec();
    }

    private ModConfigSpec buildConfigSpec() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ConfigBuilder.build(builder, this, this.config);
        this.configValueCallbacks = ImmutableList.copyOf(this.configValueCallbacks);
        return builder.build();
    }

    public final String getFileName() {
        Objects.requireNonNull(this.fileName, "file name is null");
        return this.fileName;
    }

    public final void setFileNameFactory(UnaryOperator<String> fileNameFactory) {
        Objects.requireNonNull(fileNameFactory, "file name factory is null");
        this.fileNameFactory = fileNameFactory;
    }

    public enum ModConfigEventType {
        LOADING,
        RELOADING,
        UNLOADING;

        public boolean isLoading() {
            return this == LOADING || this == RELOADING;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private enum ModConfigStatus {
        CONFIG_MISSING("Mod config is missing"),
        DATA_MISSING("Config data is missing"),
        FULLY_LOADED(null);

        @Nullable
        private final String message;

        ModConfigStatus(@Nullable String message) {
            this.message = message;
        }

        public void throwIfNecessary(Object o) {
            Objects.requireNonNull(o, "config is null");
            Objects.requireNonNull(this.message, "message is null");
            PuzzlesLib.LOGGER.error(
                    "Calling config at {} when it is not yet available. This is a harmless oversight, please report to the author. {}",
                    o.getClass(),
                    this.message,
                    new Exception("Config not yet available"));
        }
    }
}
