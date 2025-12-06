package fuzs.puzzleslib.neoforge.api.data.v2.core;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An enhanced implementation of {@link DataProviderContext} that also holds an {@link ExistingFileHelper} instance.
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
     * The file helper.
     */
    private final ExistingFileHelper fileHelper;

    /**
     * @param modId      the generating mod id
     * @param packOutput the pack output instance
     * @param registries the registry lookup provider
     * @param fileHelper the file helper
     */
    public NeoForgeDataProviderContext(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, ResourceManager clientResourceManager, ResourceManager serverResourceManager, ExistingFileHelper fileHelper) {
        super(modId, packOutput, registries);
        this.clientResourceManager = clientResourceManager;
        this.serverResourceManager = serverResourceManager;
        this.fileHelper = fileHelper;
    }

    /**
     * Creates a proper context from the corresponding NeoForge event to be used in actual data-generation.
     *
     * @param event the event
     * @return the new data provider context
     */
    public static NeoForgeDataProviderContext fromEvent(GatherDataEvent event) {
        return fromEvent(event, event.getGenerator().getPackOutput(), event.getLookupProvider());
    }

    /**
     * Creates a proper context from the corresponding Forge event usable in actual data-generation.
     *
     * @param event      the event
     * @param registries the registry lookup provider
     * @return new context instance
     */
    public static NeoForgeDataProviderContext fromEvent(GatherDataEvent event, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        return new NeoForgeDataProviderContext(event.getModContainer().getModId(),
                packOutput,
                registries,
                event.getResourceManager(PackType.CLIENT_RESOURCES),
                event.getResourceManager(PackType.SERVER_DATA),
                event.getExistingFileHelper());
    }

    @Override
    public ResourceManager getClientResourceManager() {
        return this.clientResourceManager;
    }

    @Override
    public ResourceManager getServerResourceManager() {
        return this.serverResourceManager;
    }

    @Override
    public NeoForgeDataProviderContext withRegistries(CompletableFuture<HolderLookup.Provider> registries) {
        return new NeoForgeDataProviderContext(this.getModId(),
                this.getPackOutput(),
                registries,
                this.clientResourceManager,
                this.serverResourceManager,
                this.fileHelper);
    }

    /**
     * @return the file helper
     */
    public ExistingFileHelper getFileHelper() {
        return this.fileHelper;
    }

    /**
     * A simple shortcut for a data provider factory requiring an instance of this context, helps with complaints about
     * parametrized varargs.
     */
    @FunctionalInterface
    public interface Factory extends Function<NeoForgeDataProviderContext, DataProvider> {
        // NO-OP
    }

    /**
     * Another factory to support the old data providers that have a constructor taking the Forge event directly with an
     * additional mod id.
     */
    @FunctionalInterface
    public interface LegacyFactory extends BiFunction<GatherDataEvent, String, DataProvider> {
        // NO-OP
    }
}
