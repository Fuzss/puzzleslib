package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Allows for registering modifications (including additions and removals) to biomes loaded from the current data pack.
 */
public interface BiomeModificationsContext {

    /**
     * Add a modification to this context.
     *
     * @param biomeLoadingPhase the loading phase, useful to separate additions and removals
     * @param biomeSelector     the selection context for current biome
     * @param biomeModifier     the modification context
     */
    @Deprecated
    void register(BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier);

    /**
     * Add a modification to this context.
     *
     * @param biomeLoadingPhase the loading phase, useful to separate additions and removals
     * @param biomeSelector     the selection context for current biome
     * @param biomeModifier     the modification context
     */
    void registerBiomeModification(BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier);
}
