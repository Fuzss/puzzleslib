package fuzs.puzzleslib.config;

import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * just a very basic template for implementing {@link ConfigDataHolderV2} in the common project
 * @param <T> config type
 */
abstract class ConfigDataHolderImplV2<T extends AbstractConfig> implements ConfigDataHolderV2<T> {
    /**
     * the stored config
     */
    protected final T config;
    /**
     * file name, should be set to {@link ConfigHolderV2#defaultName} in mod loader specific implementation
     */
    protected UnaryOperator<String> fileName;
    /**
     * custom syncs to perform when config reloads
     */
    protected final List<Runnable> additionalCallbacks = Lists.newArrayList();
    /**
     * loading stage for config, useful to determine if it has properly been loaded when used (for finding bugs of early access)
     */
    protected ConfigLoadStageV2 loadStage = ConfigLoadStageV2.NOT_PRESENT;

    /**
     * @param config config factory
     */
    protected ConfigDataHolderImplV2(Supplier<T> config) {
        this.config = config.get();
    }

    @Override
    public T config() {
        this.testAvailable();
        return this.config;
    }

    @Override
    public boolean isAvailable() {
        ConfigLoadStageV2 currentLoadingStage = this.findLoadStage();
        if (currentLoadingStage != ConfigLoadStageV2.LOADED || this.loadStage != ConfigLoadStageV2.AVAILABLE) {
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
     * by default this is set to {@link ConfigHolderV2#defaultName}, otherwise {@link ConfigHolderV2#simpleName} and {@link ConfigHolderV2#moveToDir} exist for convenience
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
    protected abstract ConfigLoadStageV2 findLoadStage();
}
