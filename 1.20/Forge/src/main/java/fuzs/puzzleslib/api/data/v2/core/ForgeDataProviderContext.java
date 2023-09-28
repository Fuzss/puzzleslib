package fuzs.puzzleslib.api.data.v2.core;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * An enhanced implementation of {@link DataProviderContext} that also holds an {@link ExistingFileHelper} instance.
 */
public class ForgeDataProviderContext extends DataProviderContext {
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
    public ForgeDataProviderContext(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
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
    public static ForgeDataProviderContext fromEvent(String modId, GatherDataEvent evt) {
        return new ForgeDataProviderContext(modId, evt.getGenerator().getPackOutput(), evt.getLookupProvider(), evt.getExistingFileHelper());
    }

    /**
     * @return the file helper
     */
    public ExistingFileHelper getFileHelper() {
        return this.fileHelper;
    }
}
