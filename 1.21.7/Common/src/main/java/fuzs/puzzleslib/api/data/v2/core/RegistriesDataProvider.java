package fuzs.puzzleslib.api.data.v2.core;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * An extension for {@link net.minecraft.data.registries.RegistriesDatapackGenerator} for obtaining the updated
 * {@link HolderLookup.Provider} future.
 */
public interface RegistriesDataProvider {

    /**
     * @return the updated {@link HolderLookup.Provider} future obtained from
     *         {@link RegistrySetBuilder.PatchedRegistries#full()}
     */
    CompletableFuture<HolderLookup.Provider> getRegistries();
}
