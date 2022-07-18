package fuzs.puzzleslib.config;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.core.FabricConfigBuilderWrapper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * implementation on Fabric, identical to Forge class (needs to exist twice as it cannot exist in the common sub project)
 * @param <T> config type
 */
class FabricConfigDataHolderImplV2<T extends AbstractConfig> extends ConfigDataHolderImplV2<T> {
    /**
     * the type of config, need when registering and for the file name
     */
    private final ModConfig.Type configType;
    /**
     * the mod config instance once it has been created/registered
     */
    @Nullable
    private ModConfig modConfig;
    /**
     * immutable list of config value callbacks created from annotated configs for syncing changes
     */
    private List<Runnable> configValueCallbacks;

    /**
     * @param configType    type of config
     * @param config        config factory
     */
    public FabricConfigDataHolderImplV2(ModConfig.Type configType, Supplier<T> config) {
        super(config);
        this.configType = configType;
        this.fileName = modId -> ConfigHolderV2.defaultName(modId, configType.extension());
    }

    @Override
    protected void testAvailable() {
        if (!this.isAvailable()) {
            PuzzlesLib.LOGGER.error("Calling {} config when it is not yet available! This is a bug! Current loading stage: {}", this.configType.extension(), this.loadStage, new Exception("Config not yet available"));
        }
    }

    @Override
    protected ConfigLoadStageV2 findLoadStage() {
        return this.findLoadStage(this.config, this.modConfig);
    }

    /**
     * mod loader event on config loading/reloading
     *
     * @param config    mod config instance
     * @param reloading is the config being reloaded (only for log message)
     */
    public void onModConfig(ModConfig config, boolean reloading) {
        if (config.getType() == this.configType) {
            // not sure why null is permitted, but there probably is a reason...
            if (this.modConfig == null || config == this.modConfig) {
                this.configValueCallbacks.forEach(Runnable::run);
                // call this before running callbacks, so they may use the config already
                this.makeConfigAvailable(config);
                this.additionalCallbacks.forEach(Runnable::run);
                PuzzlesLib.LOGGER.info("{} {} config for {}", reloading ? "Reloading" : "Loading", config.getType().extension(), config.getModId());
            }
        }
    }

    /**
     * register configs, creates the {@link ModConfig}
     *
     * @param factory factory for actual ModConfig
     */
    public void register(ModConfigFactory factory) {
        if (this.modConfig != null) throw new IllegalStateException(String.format("Config for type %s has already been registered!", this.configType));
        if (this.config != null) {
            // add config reload callback first to make sure it's called when initially loading configs
            // (since on some systems reload event doesn't trigger during startup, resulting in configs only being loaded here)
            this.addCallback(this.config::afterConfigReload);
            ImmutableList.Builder<Runnable> builder = ImmutableList.builder();
            ForgeConfigSpec spec = this.buildSpec(this.config, new ConfigHolder.ConfigCallback() {

                @Override
                public <V> void accept(Supplier<V> entry, Consumer<V> save) {
                    builder.add(() -> save.accept(entry.get()));
                }
            });
            this.modConfig = factory.createAndRegister(this.configType, spec, this.fileName);
            this.configValueCallbacks = builder.build();
        }
    }

    /**
     * creates a builder and builds the config spec from it
     *
     * @param config config to build
     * @return built spec
     */
    private ForgeConfigSpec buildSpec(AbstractConfig config, ConfigHolder.ConfigCallback saveCallback) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        config.setupConfig(new FabricConfigBuilderWrapper(builder), saveCallback);
        return builder.build();
    }

    /**
     * tries to set client loading stage to {@link ConfigLoadStageV2#AVAILABLE}
     *
     * @param modConfig the mod config object to check, do not use global field as it might not have been set yet since the loading event is called inside the ModConfig constructor on Fabric
     */
    private void makeConfigAvailable(@Nullable ModConfig modConfig) {
        ConfigLoadStageV2 currentLoadingStage = this.findLoadStage(this.config, modConfig);
        if (currentLoadingStage == ConfigLoadStageV2.LOADED) {
            currentLoadingStage = ConfigLoadStageV2.AVAILABLE;
        }
        this.loadStage = currentLoadingStage;
    }

    /**
     * @param config config object for this config type
     * @param modConfig mod config object for this config type
     * @return loading stage corresponding to state of <code>config</code> and <code>modConfig</code>
     */
    private ConfigLoadStageV2 findLoadStage(@Nullable AbstractConfig config, @Nullable ModConfig modConfig) {
        if (config == null) {
            return ConfigLoadStageV2.NOT_PRESENT;
        } else if (modConfig == null) {
            return ConfigLoadStageV2.INITIALIZED;
        } else if (modConfig.getConfigData() == null) {
            return ConfigLoadStageV2.MISSING_DATA;
        }
        return ConfigLoadStageV2.LOADED;
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
