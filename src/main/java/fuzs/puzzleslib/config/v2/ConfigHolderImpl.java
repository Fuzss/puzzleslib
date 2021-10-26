package fuzs.puzzleslib.config.v2;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * client config will only be created on physical client
     * @param client client config factory
     * @param server server config factory
     */
    ConfigHolderImpl(@Nonnull Supplier<C> client, @Nonnull Supplier<S> server) {
        this.client = FMLEnvironment.dist.isClient() ? client.get() : null;
        this.server = server.get();
    }

    /**
     * register configs if present
     * @param context mod context to register to
     */
    public void addConfigs(ModLoadingContext context) {
        if (this.client != null) {
            context.registerConfig(ModConfig.Type.CLIENT, this.buildSpec(this.client));
        }
        if (this.server != null) {
            context.registerConfig(ModConfig.Type.SERVER, this.buildSpec(this.server));
        }
    }

    /** register configs, allows for custom file names
     * @param context mod context to register to
     * @param clientName client config file name
     * @param serverName server config file name
     */
    public void addConfigs(ModLoadingContext context, String clientName, String serverName) {
        if (this.client != null) {
            context.registerConfig(ModConfig.Type.CLIENT, this.buildSpec(this.client), clientName);
        }
        if (this.server != null) {
            context.registerConfig(ModConfig.Type.SERVER, this.buildSpec(this.server), serverName);
        }
    }

    /**
     * creates a builder and buildes the config from it
     * @param config config to build
     * @return built spec
     */
    private ForgeConfigSpec buildSpec(AbstractConfig config) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        config.setupConfig(builder);
        return builder.build();
    }

    @Override
    public C client() {
        return this.client;
    }

    @Override
    public S server() {
        return this.server;
    }
}
