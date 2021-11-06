package fuzs.puzzleslib.config;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.core.PuzzlesLibMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * implementation of {@link ConfigHolder} for building configs and handling config creation depending on physical side
 * @param <C> client config type
 * @param <S> server config type
 */
public class ConfigHolderImpl<C extends AbstractConfig, S extends AbstractConfig> implements ConfigHolder<C, S> {
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
     * sync value field when client config reloads
     */
    private final List<Runnable> clientCallbacks = Lists.newArrayList();
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
     * client config will only be created on physical client
     * @param client client config factory
     * @param server server config factory
     */
    ConfigHolderImpl(@Nonnull Supplier<C> client, @Nonnull Supplier<S> server) {
        this.client = FMLEnvironment.dist.isClient() ? client.get() : null;
        this.server = server.get();
    }

    /**
     * @param evt forge config event
     * @param modId mod id for this config holder
     */
    @SubscribeEvent
    public void onModConfig(final ModConfigEvent evt, String modId) {
        // this is fired on ModEventBus, so mod id check is not necessary here
        // we keep this as it's required on Fabric though due to a dedicated ModEventBus being absent
        if (evt.getConfig().getModId().equals(modId)) {
            final ModConfig.Type type = evt.getConfig().getType();
            switch (type) {
                case CLIENT -> this.clientCallbacks.forEach(Runnable::run);
                case SERVER -> this.serverCallbacks.forEach(Runnable::run);
                case COMMON -> throw new RuntimeException("Common config type not supported");
            }
            if (evt instanceof ModConfigEvent.Reloading) {
                PuzzlesLibMod.LOGGER.info("Reloading {} config for {}", type.extension(), modId);
            }
        }
    }

    /**
     * register config entry for <code>type</code>
     * @param type  config type, only client and server supported
     * @param entry source config value object
     * @param save action to perform when value changes (is reloaded)
     * @param <T> type for value
     */
    private  <T> void addSaveCallback(ModConfig.Type type, ForgeConfigSpec.ConfigValue<T> entry, Consumer<T> save) {
        switch (type) {
            case CLIENT -> this.clientCallbacks.add(() -> save.accept(entry.get()));
            case SERVER -> this.serverCallbacks.add(() -> save.accept(entry.get()));
            case COMMON -> throw new RuntimeException("Common config type not supported");
        }
    }

    /**
     * register config event {@link #onModConfig} and configs themselves for <code>modId</code>
     * @param modId modId to register for
     */
    public void addConfigs(String modId) {
        this.addConfigs(ModLoadingContext.get());
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((final ModConfigEvent evt) -> this.onModConfig(evt, modId));
        // ModConfigEvent sometimes doesn't fire on start-up, resulting in config values not being synced, so we force it once
        // not sure if this is still an issue though
//        modBus.addListener((final FMLLoadCompleteEvent evt) -> {
//            this.clientCallbacks.forEach(Runnable::run);
//            this.serverCallbacks.forEach(Runnable::run);
//        });
    }

    /**
     * register configs
     * @param context mod context to register to
     */
    private void addConfigs(ModLoadingContext context) {
        if (this.client != null) {
            // can't use a lambda expression for a functional interface, if the method in the functional interface has type parameters
            final ConfigCallback saveCallback = new ConfigCallback() {
                @Override
                public <T> void accept(ForgeConfigSpec.ConfigValue<T> entry, Consumer<T> save) {
                    ConfigHolderImpl.this.addSaveCallback(ModConfig.Type.CLIENT, entry, save);
                }
            };
            if (this.clientFileName.isEmpty()) {
                context.registerConfig(ModConfig.Type.CLIENT, this.buildSpec(this.client, saveCallback));
            } else {
                context.registerConfig(ModConfig.Type.CLIENT, this.buildSpec(this.client, saveCallback), this.clientFileName);
            }
            this.addClientCallback(this.client::afterConfigReload);
        }
        if (this.server != null) {
            final ConfigCallback saveCallback = new ConfigCallback() {
                @Override
                public <T> void accept(ForgeConfigSpec.ConfigValue<T> entry, Consumer<T> save) {
                    ConfigHolderImpl.this.addSaveCallback(ModConfig.Type.SERVER, entry, save);
                }
            };
            if (this.serverFileName.isEmpty()) {
                context.registerConfig(ModConfig.Type.SERVER, this.buildSpec(this.server, saveCallback));
            } else {
                context.registerConfig(ModConfig.Type.SERVER, this.buildSpec(this.server, saveCallback), this.serverFileName);
            }
            this.addServerCallback(this.server::afterConfigReload);
        }
    }

    /**
     * creates a builder and buildes the config from it
     * @param config config to build
     * @return built spec
     */
    private ForgeConfigSpec buildSpec(AbstractConfig config, ConfigCallback saveCallback) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        config.setupConfig(builder, saveCallback);
        return builder.build();
    }

    /**
     * @param fileName file name for client
     * @return this
     */
    public ConfigHolderImpl<C, S> setClientFileName(String fileName) {
        this.clientFileName = fileName;
        return this;
    }

    /**
     * @param fileName file name for server
     * @return this
     */
    public ConfigHolderImpl<C, S> setServerFileName(String fileName) {
        this.serverFileName = fileName;
        return this;
    }

    @Override
    public C client() {
        return this.client;
    }

    @Override
    public S server() {
        return this.server;
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
