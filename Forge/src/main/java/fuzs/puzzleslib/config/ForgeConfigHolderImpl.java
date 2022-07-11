package fuzs.puzzleslib.config;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.core.ForgeConfigBuilderWrapper;
import fuzs.puzzleslib.core.DistType;
import fuzs.puzzleslib.core.DistTypeExecutor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * implementation of {@link ConfigHolder} for building configs and handling config creation depending on physical side
 * heavily depends on mod loader, therefore not very much is worth abstracting
 * @param <C> client config type
 * @param <S> server config type
 */
public class ForgeConfigHolderImpl<C extends AbstractConfig, S extends AbstractConfig> implements ConfigHolder<C, S> {
    /**
     * client config
     */
    @Nullable
    private final C client;
    /**
     * server config
     */
    @Nullable
    private final S server;
    /**
     * client callbacks for annotated config values, separate from {@link #clientCallbacks} to guarantee they run before those
     */
    private final List<Runnable> clientConfigValueCallbacks = Lists.newArrayList();
    /**
     * sync value field when client config reloads
     */
    private final List<Runnable> clientCallbacks = Lists.newArrayList();
    /**
     * server callbacks for annotated config values, separate from {@link #serverCallbacks} to guarantee they run before those
     */
    private final List<Runnable> serverConfigValueCallbacks = Lists.newArrayList();
    /**
     * sync value field when server config reloads
     */
    private final List<Runnable> serverCallbacks = Lists.newArrayList();
    /**
     * client config file name, empty for default name
     */
    private String clientFileName = "";
    /**
     * server config file name, empty for default name
     */
    private String serverFileName = "";
    /**
     * the client mod config object for checking if config data is available
     */
    @Nullable
    private ModConfig clientModConfig;
    /**
     * the server mod config object for checking if config data is available
     */
    @Nullable
    private ModConfig serverModConfig;
    /**
     * loading stage of client config
     */
    private ConfigLoadingStage clientLoadingStage = ConfigLoadingStage.NOT_PRESENT;
    /**
     * loading stage of server config
     */
    private ConfigLoadingStage serverLoadingStage = ConfigLoadingStage.NOT_PRESENT;

    /**
     * client config will only be created on physical client
     * @param client client config factory
     * @param server server config factory
     */
    public ForgeConfigHolderImpl(@NotNull Supplier<C> client, @NotNull Supplier<S> server) {
        this.client = DistTypeExecutor.getWhenOn(DistType.CLIENT, () -> client);
        this.server = server.get();
    }

    /**
     * @param config    config instance
     * @param modId mod id for this config holder
     * @param reloading is the config being reloaded (only for log message)
     */
    private void onModConfig(ModConfig config, String modId, boolean reloading) {
        // this is fired on ModEventBus, so mod id check is not necessary here
        // we keep this as it's required on Fabric though due to a dedicated ModEventBus being absent
        if (config.getModId().equals(modId)) {
            switch (config.getType()) {
                case CLIENT -> {
                    if (config == this.clientModConfig || this.clientModConfig == null) {
                        this.clientConfigValueCallbacks.forEach(Runnable::run);
                        // call this before running callbacks, so they may use the config already
                        this.makeClientAvailable(config);
                        this.clientCallbacks.forEach(Runnable::run);
                    }
                }
                case SERVER -> {
                    if (config == this.serverModConfig || this.serverModConfig == null) {
                        this.serverConfigValueCallbacks.forEach(Runnable::run);
                        // call this before running callbacks, so they may use the config already
                        this.makeServerAvailable(config);
                        this.serverCallbacks.forEach(Runnable::run);
                    }
                }
            }
            PuzzlesLib.LOGGER.info("{} {} config for {}", reloading ? "Reloading" : "Loading", config.getType().extension(), modId);
        }
    }

    /**
     * register config entry for <code>type</code>
     * @param type  config type, only client and server supported
     * @param entry source config value object
     * @param save action to perform when value changes (is reloaded)
     * @param <T> type for value
     */
    private <T> void addSaveCallback(ModConfig.Type type, Supplier<T> entry, Consumer<T> save) {
        switch (type) {
            case CLIENT -> this.clientConfigValueCallbacks.add(() -> save.accept(entry.get()));
            case SERVER -> this.serverConfigValueCallbacks.add(() -> save.accept(entry.get()));
            case COMMON -> throw new RuntimeException("Common config type not supported");
        }
    }

    @Override
    public void loadConfigs(String modId) {
        // register events before registering configs
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((final ModConfigEvent evt) -> this.onModConfig(evt.getConfig(), modId, evt instanceof ModConfigEvent.Reloading));
        this.registerConfigs(ModLoadingContext.get());
    }

    /**
     * register configs
     * @param context mod context to register to
     */
    private void registerConfigs(ModLoadingContext context) {
        // add config reload callback first to make sure it's called when initially loading configs (since on some systems reload event doesn't trigger during startup, resulting in configs only being loaded here)
        if (this.client != null) {
            this.addClientCallback(this.client::afterConfigReload);
            this.registerConfig(context, ModConfig.Type.CLIENT, this.client, this.clientFileName);
        }
        if (this.server != null) {
            this.addServerCallback(this.server::afterConfigReload);
            this.registerConfig(context, ModConfig.Type.SERVER, this.server, this.serverFileName);
        }
    }

    /**
     * register config and reloading callbacks
     * @param context mod context to register to
     * @param type client or server config type
     * @param config the config object
     * @param fileName file name, might be empty, will use default name then
     */
    private void registerConfig(ModLoadingContext context, ModConfig.Type type, AbstractConfig config, String fileName) {
        // can't use a lambda expression for a functional interface, if the method in the functional interface has type parameters
        final ConfigCallback saveCallback = new ConfigCallback() {
            @Override
            public <T> void accept(Supplier<T> entry, Consumer<T> save) {
                ForgeConfigHolderImpl.this.addSaveCallback(type, entry, save);
            }
        };
        ModConfig modConfig;
        if (StringUtils.isEmpty(fileName)) {
            modConfig = new ModConfig(type, this.buildSpec(config, saveCallback), context.getActiveContainer());
        } else {
            modConfig = new ModConfig(type, this.buildSpec(config, saveCallback), context.getActiveContainer(), fileName);
        }
        context.getActiveContainer().addConfig(modConfig);
        switch (type) {
            case CLIENT -> this.clientModConfig = modConfig;
            case SERVER -> this.serverModConfig = modConfig;
            case COMMON -> throw new RuntimeException("Common config type not supported");
        }
    }

    /**
     * creates a builder and buildes the config from it
     * @param config config to build
     * @return built spec
     */
    private ForgeConfigSpec buildSpec(AbstractConfig config, ConfigCallback saveCallback) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        config.setupConfig(new ForgeConfigBuilderWrapper(builder), saveCallback);
        return builder.build();
    }

    /**
     * tries to set client loading stage to {@link ConfigLoadingStage#AVAILABLE}
     * @param modConfig the mod config object to check, do not use global field as it might not have been set yet since the loading event is called inside the ModConfig constructor on Fabric
     */
    private void makeClientAvailable(@Nullable ModConfig modConfig) {
        ConfigLoadingStage currentLoadingStage = this.currentLoadingStage(this.client, modConfig);
        if (currentLoadingStage == ConfigLoadingStage.LOADED) {
            currentLoadingStage = ConfigLoadingStage.AVAILABLE;
        }
        this.clientLoadingStage = currentLoadingStage;
    }

    /**
     * tries to set server loading stage to {@link ConfigLoadingStage#AVAILABLE}
     * @param modConfig the mod config object to check, do not use global field as it might not have been set yet since the loading event is called inside the ModConfig constructor on Fabric
     */
    private void makeServerAvailable(@Nullable ModConfig modConfig) {
        ConfigLoadingStage currentLoadingStage = this.currentLoadingStage(this.server, modConfig);
        if (currentLoadingStage == ConfigLoadingStage.LOADED) {
            currentLoadingStage = ConfigLoadingStage.AVAILABLE;
        }
        this.serverLoadingStage = currentLoadingStage;
    }

    /**
     * @param config config object for this config type
     * @param modConfig mod config object for this config type
     * @return loading stage corresponding to state of <code>config</code> and <code>modConfig</code>
     */
    private ConfigLoadingStage currentLoadingStage(@Nullable AbstractConfig config, @Nullable ModConfig modConfig) {
        if (config == null) {
            return ConfigLoadingStage.NOT_PRESENT;
        } else if (modConfig == null) {
            return ConfigLoadingStage.INITIALIZED;
        } else if (modConfig.getConfigData() == null) {
            return ConfigLoadingStage.MISSING_DATA;
        }
        return ConfigLoadingStage.LOADED;
    }

    @Override
    public C client() {
        if (!this.isClientAvailable()) {
            PuzzlesLib.LOGGER.error("Calling client config when it is not yet available! This is a bug! Current loading stage: {}", this.clientLoadingStage, new Exception("Client config not yet available"));
        }
        return this.client;
    }

    @Override
    public S server() {
        if (!this.isServerAvailable()) {
            PuzzlesLib.LOGGER.error("Calling server config when it is not yet available! This is a bug! Current loading stage: {}", this.serverLoadingStage, new Exception("Server config not yet available"));
        }
        return this.server;
    }

    @Override
    public boolean isClientAvailable() {
        ConfigLoadingStage currentLoadingStage = this.currentLoadingStage(this.client, this.clientModConfig);
        if (currentLoadingStage != ConfigLoadingStage.LOADED || this.clientLoadingStage != ConfigLoadingStage.AVAILABLE) {
            this.clientLoadingStage = currentLoadingStage;
            return false;
        }
        return true;
    }

    @Override
    public boolean isServerAvailable() {
        ConfigLoadingStage currentLoadingStage = this.currentLoadingStage(this.server, this.serverModConfig);
        if (currentLoadingStage != ConfigLoadingStage.LOADED || this.serverLoadingStage != ConfigLoadingStage.AVAILABLE) {
            this.serverLoadingStage = currentLoadingStage;
            return false;
        }
        return true;
    }

    @Override
    public ConfigHolder<C, S> setClientFileName(String fileName) {
        this.clientFileName = fileName;
        return this;
    }

    @Override
    public ConfigHolder<C, S> setServerFileName(String fileName) {
        this.serverFileName = fileName;
        return this;
    }

    @Override
    public void addClientCallback(Runnable callback) {
        this.clientCallbacks.add(callback);
    }

    @Override
    public void addServerCallback(Runnable callback) {
        this.serverCallbacks.add(callback);
    }
}
