package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.level.biome.Biome;

/**
 * The modification context for the biomes weather properties.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically
 * <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext$WeatherContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification
 * system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public interface ClimateSettingsContext {

    /**
     * @see Biome#hasPrecipitation()
     * @see Biome.BiomeBuilder#hasPrecipitation(boolean)
     */
    void hasPrecipitation(boolean hasPrecipitation);

    /**
     * @see Biome#hasPrecipitation()
     * @see Biome.BiomeBuilder#hasPrecipitation(boolean)
     */
    boolean hasPrecipitation();

    /**
     * @see Biome#getBaseTemperature()
     * @see Biome.BiomeBuilder#temperature(float)
     */
    void setTemperature(float temperature);

    /**
     * @see Biome#getBaseTemperature()
     * @see Biome.BiomeBuilder#temperature(float)
     */
    float getTemperature();

    /**
     * @see Biome.BiomeBuilder#temperatureAdjustment(Biome.TemperatureModifier)
     */
    void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

    /**
     * @see Biome.BiomeBuilder#downfall(float)
     */
    void setDownfall(float downfall);
}
