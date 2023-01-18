package fuzs.puzzleslib.config;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * just a very basic template for implementing {@link ConfigDataHolder} in the common project
 * @param <T> config type
 */
public abstract class ConfigDataHolderImpl<T extends ConfigCore> implements ConfigDataHolder<T>, ValueCallback {
    /**
     * the stored config
     */
    protected final T config;
    /**
     * supplier for default config if needed at some point
     */
    private final Supplier<T> defaultConfigSupplier;
    /**
     * stored default config, null be default, only created when needed
     */
    @Nullable
    private T defaultConfig;
    /**
     * file name, should be set to {@link ConfigHolder#defaultName} in mod loader specific implementation
     */
    protected UnaryOperator<String> fileName;
    /**
     * custom syncs to perform when config reloads
     */
    protected final List<Consumer<T>> additionalCallbacks = Lists.newArrayList();
    /**
     * list of config value callbacks created from annotated configs for syncing changes
     * this is replaced with an immutable list after config setup is done
     */
    protected List<Runnable> configValueCallbacks = Lists.newArrayList();
    /**
     * loading stage for config, useful to determine if it has properly been loaded when used (for finding bugs of early access)
     */
    protected boolean available;

    /**
     * @param config config factory
     */
    protected ConfigDataHolderImpl(Supplier<T> config) {
        this.config = config.get();
        this.defaultConfigSupplier = config;
    }

    @Override
    public T config() {
        return this.isAvailable() ? this.config : this.getOrCreateDefaultConfig();
    }

    /**
     * gets the default config, creates it first and calls {@link #testAvailable()} when not present yet
     * <p>set up this way to ensure only one error message is printed to the log to avoid spam
     *
     * @return the config with default values
     */
    private T getOrCreateDefaultConfig() {
        if (this.defaultConfig == null) {
            this.testAvailable();
            this.defaultConfig = this.defaultConfigSupplier.get();
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
    public <V> void accept(Supplier<V> entry, Consumer<V> save) {
        this.configValueCallbacks.add(() -> save.accept(entry.get()));
    }

    /**
     * by default this is set to {@link ConfigHolder#defaultName}, otherwise {@link ConfigHolder#simpleName} and {@link ConfigHolder#moveToDir} exist for convenience
     *
     * @param fileName file name for this config
     */
    public void setFileName(UnaryOperator<String> fileName) {
        this.fileName = fileName;
    }

    /**
     * only called in {@link #config()} to print a full exception in the log showing where this config has been used too early
     */
    protected abstract void testAvailable();

    /**
     * abstraction for determining a possible error due to missing data when accessing configs externally
     *
     * @return the error message if present
     */
    protected abstract Either<Unit, String> findErrorMessage();
}
