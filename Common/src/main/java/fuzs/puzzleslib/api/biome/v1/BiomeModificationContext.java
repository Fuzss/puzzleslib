package fuzs.puzzleslib.api.biome.v1;

import fuzs.puzzleslib.api.core.v1.ModConstructor;

/**
 * Context containing all biome related information passed in {@link ModConstructor.BiomeModificationsContext}.
 *
 * @param climateSettings The modification context for the biomes weather properties.
 * @param specialEffects The modification context for the biomes effects.
 * @param generationSettings The modification context for the biomes generation settings.
 * @param mobSpawnSettings The modification context for the biomes spawn settings.
 */
public record BiomeModificationContext(ClimateSettingsContext climateSettings, SpecialEffectsContext specialEffects, GenerationSettingsContext generationSettings, MobSpawnSettingsContext mobSpawnSettings) {

}
