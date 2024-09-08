package fuzs.puzzleslib.impl.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.config.annotation.ConfigBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private boolean isAvailable;
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
            this.testAvailable();
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
        return this.config != null && this.findErrorMessage().left().isPresent();
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

    private void testAvailable() {
        this.findErrorMessage().ifRight(message -> {
            PuzzlesLib.LOGGER.error(
                    "Calling config at {} when it is not yet available. This is a harmless oversight, please report to the author. {}",
                    this.getFileName(), message, new Exception("Config not yet available")
            );
        });
    }

    protected Either<Unit, String> findErrorMessage() {
        if (this.fileName == null) {
            return Either.right("Mod config is missing");
        } else if (!this.isAvailable) {
            return Either.right("Config data is missing");
        } else {
            return Either.left(Unit.INSTANCE);
        }
    }

    protected void onModConfig(ModConfigEventType eventType, String fileName, Runnable removeWatch) {
        if (Objects.equals(fileName, this.getFileName())) {
            PuzzlesLib.LOGGER.info("Dispatching {} event for config {}", eventType, fileName);
            Objects.requireNonNull(this.config, () -> "config is null: " + fileName);
            if (eventType.isLoading()) {
                this.configValueCallbacks.forEach(Runnable::run);
                // set this only after callbacks have run, to ensure nothing is null anymore when the config reports as available in case of some concurrency issues
                this.isAvailable = true;
                for (Consumer<T> callback : this.additionalCallbacks) {
                    callback.accept(this.config);
                }
            } else {
                this.isAvailable = false;
            }
            if (eventType != ModConfigEventType.RELOADING) {
                this.consecutiveConfigReloads = 0;
            } else if (++this.consecutiveConfigReloads >= MAX_CONSECUTIVE_CONFIG_RELOADS) {
                // remove file watcher for configs that reload too often consecutively,
                // this is sometimes caused by issues with the file system and pointlessly creates new config files repeatedly
                // this is also triggered after editing the config in-game, but if the user is doing that they are unlikely to also edit the file in the same session
                removeWatch.run();
            }
        }
    }

    protected ModConfigSpec setupConfigSpec(String modId, String configType) {
        this.initializeFileName(modId);
        ModConfigSpec configSpec = this.buildConfigSpec();
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ConfigTranslationsManager.addConfig(modId, this.getFileName(), configType);
            this.addConfigSpecTranslations(modId, configSpec.getSpec(), new ArrayList<>(), configSpec);
        }
        return configSpec;
    }

    private void initializeFileName(String modId) {
        Objects.requireNonNull(this.config, "Attempting to register invalid config for " + modId);
        if (this.fileName == null) {
            this.fileName = this.fileNameFactory.apply(modId);
        } else {
            throw new IllegalStateException("Config has already been registered at " + this.getFileName());
        }
    }

    private ModConfigSpec buildConfigSpec() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ConfigBuilder.build(builder, this, this.config);
        this.configValueCallbacks = ImmutableList.copyOf(this.configValueCallbacks);
        return builder.build();
    }

    private void addConfigSpecTranslations(String modId, UnmodifiableConfig config, List<String> path, ModConfigSpec configSpec) {
        for (Map.Entry<String, Object> entry : config.valueMap().entrySet()) {
            ConfigTranslationsManager.addConfigValue(modId, entry.getKey());
            String comment;
            if (entry.getValue() instanceof ModConfigSpec.ValueSpec valueSpec) {
                comment = valueSpec.getComment();
            } else if (entry.getValue() instanceof UnmodifiableConfig) {
                path = new ArrayList<>(path);
                path.add(entry.getKey());
                comment = configSpec.getLevelComment(path);
                this.addConfigSpecTranslations(modId, (UnmodifiableConfig) entry.getValue(), path, configSpec);
            } else {
                comment = null;
            }
            ConfigTranslationsManager.addConfigValueComment(modId, entry.getKey(), comment);
        }
    }

    public String getFileName() {
        Objects.requireNonNull(this.fileName, "file name is null");
        return this.fileName;
    }

    public void setFileNameFactory(UnaryOperator<String> fileNameFactory) {
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
}
