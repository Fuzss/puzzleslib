package fuzs.puzzleslib.neoforge.api.data.v2.core;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
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
     * The file helper.
     */
    private final ExistingFileHelper fileHelper;

    /**
     * @param modId          the generating mod id
     * @param packOutput     the pack output instance
     * @param lookupProvider registry lookup provider
     * @param fileHelper     the file helper
     */
    public NeoForgeDataProviderContext(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(modId, packOutput, () -> lookupProvider);
        this.fileHelper = fileHelper;
    }

    /**
     * Creates a proper context from the corresponding Forge event usable in actual data-generation.
     *
     * @param modId the generating mod id
     * @param evt   the Forge event
     * @return new context instance
     */
    public static NeoForgeDataProviderContext fromEvent(String modId, GatherDataEvent evt) {
        return new NeoForgeDataProviderContext(modId, evt.getGenerator().getPackOutput(), evt.getLookupProvider(), evt.getExistingFileHelper());
    }

    /**
     * @return the file helper
     */
    public ExistingFileHelper getFileHelper() {
        return this.fileHelper;
    }

    /**
     * A simple shortcut for a data provider factory requiring an instance of this context, helps with complaints about parametrized varargs.
     */
    @FunctionalInterface
    public interface Factory extends Function<NeoForgeDataProviderContext, DataProvider> {
        // NO-OP
    }

    /**
     * Another factory to support the old data providers that have a constructor taking the Forge event directly with an additional mod id.
     */
    @FunctionalInterface
    public interface LegacyFactory extends BiFunction<GatherDataEvent, String, DataProvider> {
        // NO-OP
    }
}
