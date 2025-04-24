package fuzs.puzzleslib.neoforge.api.data.v2.core;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * An enhanced implementation of {@link DataProviderContext} that provides various
 * {@link ResourceManager ResourceManagers} for verifying the existence of used resources.
 */
public class NeoForgeDataProviderContext extends DataProviderContext {
    /**
     * The client resource manager.
     */
    private final ResourceManager clientResourceManager;
    /**
     * The server resource manager.
     */
    private final ResourceManager serverResourceManager;

    /**
     * @param modId                 the generating mod id
     * @param packOutput            the pack output instance
     * @param registries            the registry lookup provider
     * @param clientResourceManager the client resource manager
     * @param serverResourceManager the server resource manager
     */
    public NeoForgeDataProviderContext(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, ResourceManager clientResourceManager, ResourceManager serverResourceManager) {
        super(modId, packOutput, registries);
        this.clientResourceManager = clientResourceManager;
        this.serverResourceManager = serverResourceManager;
    }

    /**
     * Creates a proper context from the corresponding Forge event usable in actual data-generation.
     *
     * @param evt the event
     * @return new context instance
     */
    public static NeoForgeDataProviderContext fromEvent(GatherDataEvent evt) {
        return new NeoForgeDataProviderContext(evt.getModContainer().getModId(),
                evt.getGenerator().getPackOutput(),
                evt.getLookupProvider(),
                evt.getResourceManager(PackType.CLIENT_RESOURCES),
                evt.getResourceManager(PackType.SERVER_DATA));
    }

    @Override
    public NeoForgeDataProviderContext withRegistries(CompletableFuture<HolderLookup.Provider> registries) {
        return new NeoForgeDataProviderContext(this.getModId(),
                this.getPackOutput(),
                registries,
                this.clientResourceManager,
                this.serverResourceManager);
    }

    /**
     * @return the client resource manager
     */
    public ResourceManager getClientResourceManager() {
        return this.clientResourceManager;
    }

    /**
     * @return the server resource manager
     */
    public ResourceManager getServerResourceManager() {
        return this.serverResourceManager;
    }

    /**
     * A simple shortcut for a data provider factory requiring an instance of this context, it helps with complaints
     * about parametrized varargs.
     */
    @FunctionalInterface
    public interface Factory extends Function<NeoForgeDataProviderContext, DataProvider> {
        // NO-OP
    }
}
