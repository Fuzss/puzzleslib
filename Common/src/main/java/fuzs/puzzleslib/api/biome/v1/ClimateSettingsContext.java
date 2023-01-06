package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

/**
 * The modification context for the biomes weather properties.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext$WeatherContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public interface ClimateSettingsContext {

    /**
     * @see Biome#getPrecipitation()
     * @see Biome.BiomeBuilder#precipitation(Biome.Precipitation)
     */
    void setPrecipitation(@NotNull Biome.Precipitation precipitation);

    /**
     * @see Biome#getPrecipitation()
     * @see Biome.BiomeBuilder#precipitation(Biome.Precipitation)
     */
    Biome.Precipitation getPrecipitation();

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
    void setTemperatureModifier(@NotNull Biome.TemperatureModifier temperatureModifier);

    /**
     * @see Biome#getDownfall()
     * @see Biome.BiomeBuilder#downfall(float)
     */
    void setDownfall(float downfall);

    /**
     * @see Biome#getDownfall()
     * @see Biome.BiomeBuilder#downfall(float)
     */
    float getDownfall();
}
