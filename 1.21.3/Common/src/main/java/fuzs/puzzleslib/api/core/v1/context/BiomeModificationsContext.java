package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Allows for registering modifications (including additions and removals) to biomes loaded from the current data pack.
 */
@FunctionalInterface
public interface BiomeModificationsContext {

    /**
     * Add a modification to this context.
     *
     * @param phase    the loading phase, mainly to separate additions and removals
     * @param selector selection context for current biome
     * @param modifier modification context
     */
    void register(BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier);
}
