package fuzs.puzzleslib.api.biome.v1;

import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;

/**
 * Context containing all biome-related information passed in {@link BiomeModificationsContext}.
 *
 * @param climateSettings    the modification context for the biome weather properties
 * @param specialEffects     the modification context for the biome effects
 * @param generationSettings the modification context for the biome generation settings
 * @param mobSpawnSettings   the modification context for the biome spawn settings
 */
public record BiomeModificationContext(ClimateSettingsContext climateSettings,
                                       SpecialEffectsContext specialEffects,
                                       GenerationSettingsContext generationSettings,
                                       MobSpawnSettingsContext mobSpawnSettings) {

}
