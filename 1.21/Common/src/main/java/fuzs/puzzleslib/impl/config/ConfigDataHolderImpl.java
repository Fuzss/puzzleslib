package fuzs.puzzleslib.impl.config;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ConfigDataHolderImpl<T extends ConfigCore> implements ConfigDataHolder<T>, ValueCallback {
    protected final T config;
    private final Supplier<T> defaultConfigSupplier;
    protected final String configTypeName;
    @Nullable
    private T defaultConfig;
    protected UnaryOperator<String> fileName;
    private final List<Consumer<T>> additionalCallbacks = new ArrayList<>();
    private List<Runnable> configValueCallbacks = new ArrayList<>();
    private boolean available;

    protected ConfigDataHolderImpl(String configTypeName, Supplier<T> supplier) {
        this.configTypeName = configTypeName;
        this.config = supplier.get();
        this.defaultConfigSupplier = supplier;
        this.fileName = modId -> ConfigHolder.defaultName(modId, configTypeName);
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
        return this.findErrorMessage().left().isPresent();
    }

    @Override
    public void accept(Consumer<T> callback) {
        this.additionalCallbacks.add(callback);
    }

    @Override
    public <S, V extends ModConfigSpec.ConfigValue<S>> V accept(V entry, Consumer<S> save) {
        Objects.requireNonNull(entry, "entry is null");
        this.acceptValueCallback(() -> save.accept(entry.get()));
        return entry;
    }

    void acceptValueCallback(Runnable runnable) {
        this.configValueCallbacks.add(runnable);
    }

    private void testAvailable() {
        this.findErrorMessage().ifRight(message -> {
            PuzzlesLib.LOGGER.error(
                    "Calling {} config when it is not yet available. This is a harmless oversight, please report to the author. {}",
                    this.configTypeName,
                    message,
                    new Exception("Config not yet available")
            );
        });
    }

    protected Either<Unit, String> findErrorMessage() {
        if (!this.available) {
            return Either.right("Config callbacks have not been loaded");
        }
        return Either.left(Unit.INSTANCE);
    }

    protected void onModConfig(String modId, boolean isLoading, String eventType) {
        Objects.requireNonNull(this.config,
                () -> "Attempting to register invalid %s config for mod %s".formatted(this.configTypeName, modId)
        );
        if (isLoading) {
            this.configValueCallbacks.forEach(Runnable::run);
            // set this only after callbacks have run, to ensure nothing is null anymore when the config reports as available in case of some concurrency issues
            this.available = true;
            for (Consumer<T> callback : this.additionalCallbacks) {
                callback.accept(this.config);
            }
        } else {
            this.available = false;
        }
        PuzzlesLib.LOGGER.info("{} {} config for {}", eventType, this.configTypeName, modId);
    }

    protected ModConfigSpec buildSpec() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        AnnotatedConfigBuilder.serialize(builder, this, this.config);
        this.configValueCallbacks = ImmutableList.copyOf(this.configValueCallbacks);
        return builder.build();
    }
}
