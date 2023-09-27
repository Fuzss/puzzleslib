package fuzs.puzzleslib.api.data.v2.core;

import com.google.common.base.Suppliers;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataProviderContext {
    private final String modId;
    private final PackOutput packOutput;
    private final Supplier<CompletableFuture<HolderLookup.Provider>> lookupProvider;

    public DataProviderContext(String modId, PackOutput packOutput, Supplier<CompletableFuture<HolderLookup.Provider>> lookupProvider) {
        this.modId = modId;
        this.packOutput = packOutput;
        this.lookupProvider = lookupProvider;
    }

    public static DataProviderContext fromModId(String modId) {
        return new DataProviderContext(modId, new PackOutput(Path.of("")), Suppliers.memoize(() -> CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor())));
    }

    public String getModId() {
        return this.modId;
    }

    public PackOutput getPackOutput() {
        return this.packOutput;
    }

    public CompletableFuture<HolderLookup.Provider> getLookupProvider() {
        return this.lookupProvider.get();
    }

    @FunctionalInterface
    public interface Factory extends Function<DataProviderContext, DataProvider> {

    }
}
