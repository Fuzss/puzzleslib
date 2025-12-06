package fuzs.puzzleslib.api.data.v2.core;

import com.google.common.base.Suppliers;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A context class for providing instances required by a {@link DataProvider}.
 * <p>
 * Very similar to Forge's <code>net.minecraftforge.data.event.GatherDataEvent</code>.
 */
public class DataProviderContext {
    /**
     * The generating mod id.
     */
    private final String modId;
    /**
     * The pack output instance.
     */
    private final PackOutput packOutput;
    /**
     * Registry lookup provider.
     */
    private final Supplier<CompletableFuture<HolderLookup.Provider>> registries;

    /**
     * @param modId      the generating mod id
     * @param packOutput the pack output instance
     * @param registries registry lookup provider
     */
    public DataProviderContext(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        this(modId, packOutput, () -> registries);
    }

    /**
     * @param modId      the generating mod id
     * @param packOutput the pack output instance
     * @param registries registry lookup provider
     */
    private DataProviderContext(String modId, PackOutput packOutput, Supplier<CompletableFuture<HolderLookup.Provider>> registries) {
        this.modId = modId;
        this.packOutput = packOutput;
        this.registries = registries;
    }

    /**
     * Creates a dummy-like context instance useful for runtime generation in conjunction with
     * {@link fuzs.puzzleslib.api.resources.v1.DynamicPackResources}.
     *
     * @param modId the generating mod id
     * @return new context instance
     */
    public static DataProviderContext fromModId(String modId) {
        return fromModId(modId, Path.of(""));
    }

    /**
     * Creates a dummy-like context instance useful for runtime generation in conjunction with
     * {@link fuzs.puzzleslib.api.resources.v1.DynamicPackResources}.
     *
     * @param modId the generating mod id
     * @param path  output path
     * @return new context instance
     */
    public static DataProviderContext fromModId(String modId, Path path) {
        return new DataProviderContext(modId,
                new PackOutput(path),
                Suppliers.memoize(() -> CompletableFuture.supplyAsync(VanillaRegistries::createLookup,
                        Util.backgroundExecutor())));
    }

    /**
     * @return the generating mod id
     */
    public String getModId() {
        return this.modId;
    }

    /**
     * @return the pack output instance
     */
    public PackOutput getPackOutput() {
        return this.packOutput;
    }

    /**
     * @return registry lookup provider
     */
    public CompletableFuture<HolderLookup.Provider> getRegistries() {
        return this.registries.get();
    }

    /**
     * @return the client resource manager
     */
    @Nullable
    public ResourceManager getClientResourceManager() {
        return null;
    }

    /**
     * @return the server resource manager
     */
    @Nullable
    public ResourceManager getServerResourceManager() {
        return null;
    }

    /**
     * Creates a new data provider context instance with a different set of registries.
     *
     * @param registries registry lookup provider
     * @return new data provider context instance
     */
    public DataProviderContext withRegistries(CompletableFuture<HolderLookup.Provider> registries) {
        return new DataProviderContext(this.modId, this.packOutput, registries);
    }

    /**
     * A simple shortcut for a data provider factory requiring an instance of this context, helps with complaints about
     * parametrized varargs.
     */
    @FunctionalInterface
    public interface Factory extends Function<DataProviderContext, DataProvider> {
        // NO-OP
    }
}
