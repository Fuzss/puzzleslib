package fuzs.puzzleslib.api.biome.v1;

/**
 * Context containing all biome related information passed in {@link fuzs.puzzleslib.core.ModConstructor.BiomeModificationsContext}.
 *
 * @param climateSettings The modification context for the biomes weather properties.
 * @param specialEffects The modification context for the biomes effects.
 * @param generationSettings The modification context for the biomes generation settings.
 * @param mobSpawnSettings The modification context for the biomes spawn settings.
 */
public record BiomeModificationContext(ClimateSettingsContext climateSettings, SpecialEffectsContext specialEffects, GenerationSettingsContext generationSettings, MobSpawnSettingsContext mobSpawnSettings) {

}
