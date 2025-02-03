package fuzs.puzzleslib.api.biome.v1;

import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;

/**
 * Context containing all biome related information passed in {@link BiomeModificationsContext}.
 *
 * @param climateSettings    the modification context for the biomes weather properties
 * @param specialEffects     the modification context for the biomes effects
 * @param generationSettings the modification context for the biomes generation settings
 * @param mobSpawnSettings   the modification context for the biomes spawn settings
 */
public record BiomeModificationContext(ClimateSettingsContext climateSettings,
                                       SpecialEffectsContext specialEffects,
                                       GenerationSettingsContext generationSettings,
                                       MobSpawnSettingsContext mobSpawnSettings) {

}
