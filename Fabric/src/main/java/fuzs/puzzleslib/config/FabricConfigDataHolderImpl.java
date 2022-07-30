package fuzs.puzzleslib.config;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.annotation.AnnotatedConfigBuilder;
import fuzs.puzzleslib.config.core.FabricConfigBuilderWrapper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * implementation on Fabric, identical to Forge class (needs to exist twice as it cannot exist in the common sub project)
 * @param <T> config type
 */
class FabricConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
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
     * @param configType    type of config
     * @param config        config factory
     */
    public FabricConfigDataHolderImpl(ModConfig.Type configType, Supplier<T> config) {
        super(config);
        this.configType = configType;
        this.fileName = modId -> ConfigHolder.defaultName(modId, configType.extension());
    }

    @Override
    protected void testAvailable() {
        if (!this.isAvailable()) {
            PuzzlesLib.LOGGER.error("Calling {} config when it is not yet available! This is a bug! Current loading stage: {}", this.configType.extension(), this.loadStage, new Exception("Config not yet available"));
        }
    }

    @Override
    protected ConfigLoadStage findLoadStage() {
        return this.findLoadStage(this.config, this.modConfig);
    }

    /**
     * mod loader event on config loading/reloading
     *
     * @param config    mod config instance
     * @param reloading is the config being reloaded (only for log message)
     */
    public void onModConfig(ModConfig config, boolean reloading) {
        // null must be permitted for config loading as the event is triggered during construction of ModConfig (before the field can even be set)
        if (config.getType() == this.configType && (this.modConfig == null || config == this.modConfig)) {
            // call this before running callbacks, so they may use the config already
            this.makeConfigAvailable(config);
            String loading;
            if (this.isAvailable()) {
                loading = reloading ? "Reloading" : "Loading";
                this.configValueCallbacks.forEach(Runnable::run);
                this.additionalCallbacks.forEach(Runnable::run);
            } else {
                loading = "Unloading";
            }
            PuzzlesLib.LOGGER.info("{} {} config for {}", loading, config.getType().extension(), config.getModId());
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
            this.modConfig = factory.createAndRegister(this.configType, this.buildSpec(), this.fileName);
        }
    }

    /**
     * creates a builder and builds the config spec from it
     *
     * @return built spec
     */
    private ForgeConfigSpec buildSpec() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        AnnotatedConfigBuilder.serialize(new FabricConfigBuilderWrapper(builder),this, this.config);
        this.configValueCallbacks = ImmutableList.copyOf(this.configValueCallbacks);
        return builder.build();
    }

    /**
     * tries to set client loading stage to {@link ConfigLoadStage#AVAILABLE}
     *
     * @param modConfig the mod config object to check, do not use global field as it might not have been set yet since the loading event is called inside the ModConfig constructor on Fabric
     */
    private void makeConfigAvailable(@Nullable ModConfig modConfig) {
        ConfigLoadStage currentLoadingStage = this.findLoadStage(this.config, modConfig);
        if (currentLoadingStage == ConfigLoadStage.LOADED) {
            currentLoadingStage = ConfigLoadStage.AVAILABLE;
        }
        this.loadStage = currentLoadingStage;
    }

    /**
     * @param config config object for this config type
     * @param modConfig mod config object for this config type
     * @return loading stage corresponding to state of <code>config</code> and <code>modConfig</code>
     */
    private ConfigLoadStage findLoadStage(@Nullable ConfigCore config, @Nullable ModConfig modConfig) {
        if (config == null) {
            return ConfigLoadStage.NOT_PRESENT;
        } else if (modConfig == null) {
            return ConfigLoadStage.INITIALIZED;
        } else if (modConfig.getConfigData() == null) {
            return ConfigLoadStage.MISSING_DATA;
        }
        return ConfigLoadStage.LOADED;
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
