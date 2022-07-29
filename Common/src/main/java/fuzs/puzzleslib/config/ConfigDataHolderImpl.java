package fuzs.puzzleslib.config;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * just a very basic template for implementing {@link ConfigDataHolder} in the common project
 * @param <T> config type
 */
abstract class ConfigDataHolderImpl<T extends AbstractConfig> implements ConfigDataHolder<T> {
    /**
     * the stored config
     */
    protected final T config;
    /**
     * file name, should be set to {@link ConfigHolder#defaultName} in mod loader specific implementation
     */
    protected UnaryOperator<String> fileName;
    /**
     * custom syncs to perform when config reloads
     */
    protected final List<Runnable> additionalCallbacks = Lists.newArrayList();
    /**
     * loading stage for config, useful to determine if it has properly been loaded when used (for finding bugs of early access)
     */
    protected ConfigLoadStage loadStage = ConfigLoadStage.NOT_PRESENT;

    /**
     * @param config config factory
     */
    protected ConfigDataHolderImpl(Supplier<T> config) {
        this.config = config.get();
    }

    @Override
    public T config() {
        this.testAvailable();
        return this.config;
    }

    @Override
    public boolean isAvailable() {
        ConfigLoadStage currentLoadingStage = this.findLoadStage();
        if (currentLoadingStage != ConfigLoadStage.LOADED || this.loadStage != ConfigLoadStage.AVAILABLE) {
            this.loadStage = currentLoadingStage;
            return false;
        }
        return true;
    }

    @Override
    public void addCallback(Runnable callback) {
        this.additionalCallbacks.add(callback);
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
     * abstraction for determining the current loading stage so it may be used in here already
     *
     * @return the current loading stage
     */
    protected abstract ConfigLoadStage findLoadStage();
}
