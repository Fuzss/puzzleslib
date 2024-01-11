package fuzs.puzzleslib.impl.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * just a very basic template for implementing {@link ConfigDataHolder} in the common project
 * @param <T> config type
 */
class ConfigDataHolderImpl<T extends ConfigCore> implements ConfigDataHolder<T>, ValueCallback {
    final T config;
    private final Supplier<T> defaultConfigSupplier;
    private final ModConfig.Type configType;
    @Nullable
    private ModConfig modConfig;
    @Nullable
    private T defaultConfig;
    UnaryOperator<String> fileName;
    private final List<Consumer<T>> additionalCallbacks = Lists.newArrayList();
    private List<Runnable> configValueCallbacks = Lists.newArrayList();
    private boolean available;

    protected ConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> supplier) {
        this.configType = configType;
        this.config = supplier.get();
        this.defaultConfigSupplier = supplier;
        this.fileName = modId -> ConfigHolder.defaultName(modId, configType.extension());
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
    public <S, V extends ForgeConfigSpec.ConfigValue<S>> V accept(V entry, Consumer<S> save) {
        Objects.requireNonNull(entry, "entry is null");
        this.acceptValueCallback(() -> save.accept(entry.get()));
        return entry;
    }

    void acceptValueCallback(Runnable runnable) {
        this.configValueCallbacks.add(runnable);
    }

    private void testAvailable() {
        this.findErrorMessage().ifRight(message -> {
            PuzzlesLib.LOGGER.error("Calling {} config when it is not yet available! This is a bug! Message: {}", this.configType.extension(), message, new Exception("Config not yet available"));
        });
    }

    private Either<Unit, String> findErrorMessage() {
        if (this.modConfig == null) {
            return Either.right("Mod config instance is missing");
        } else if (this.modConfig.getConfigData() == null) {
            return Either.right("Config data is missing");
        } else if (!this.available) {
            return Either.right("Config callbacks have not been loaded");
        }
        return Either.left(Unit.INSTANCE);
    }

    void onModConfig(ModConfig config, boolean reloading) {
        Objects.requireNonNull(this.config, "Attempting to register invalid config of type %s for mod id %s".formatted(this.configType.extension(), config.getModId()));
        // null must be permitted for config loading as the event is triggered during construction of ModConfig (before the field can even be set)
        if (config.getType() == this.configType && (this.modConfig == null || config == this.modConfig)) {
            String loading;
            if (config.getConfigData() != null) {
                loading = reloading ? "Reloading" : "Loading";
                this.configValueCallbacks.forEach(Runnable::run);
                // set this only after callbacks have run, to ensure nothing is null anymore when the config reports as available in case of some concurrency issues
                this.available = true;
                for (Consumer<T> callback : this.additionalCallbacks) {
                    callback.accept(this.config);
                }
            } else {
                loading = "Unloading";
                this.available = false;
            }
            PuzzlesLib.LOGGER.info("{} {} config for {}", loading, config.getType().extension(), config.getModId());
        }
    }

    void register(ModConfigFactory factory) {
        Objects.requireNonNull(this.config, "Attempting to register invalid config of type %s".formatted(this.configType.extension()));
        if (this.modConfig != null) throw new IllegalStateException(String.format("Config for type %s has already been registered!", this.configType));
        this.modConfig = factory.createAndRegister(this.configType, this.buildSpec(), this.fileName);
    }

    private ForgeConfigSpec buildSpec() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        AnnotatedConfigBuilder.serialize(builder,this, this.config);
        this.configValueCallbacks = ImmutableList.copyOf(this.configValueCallbacks);
        return builder.build();
    }

    /**
     * a simple abstraction interface for creating a {@link ModConfig} from provided data to allow this class to be the same on all mod loaders
     */
    interface ModConfigFactory {

        /**
         * creates and registers a new mod config instance, serves as an abstraction for Forge and Fabric
         *
         * @param type      mod config type to register this config for
         * @param spec      the built spec
         * @param fileName  file name (takes modId)
         * @return          the mod config instance
         */
        ModConfig createAndRegister(ModConfig.Type type, ForgeConfigSpec spec, UnaryOperator<String> fileName);
    }
}
