package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.level.biome.Biome;

public interface ClimateSettingsContext {

    /**
     * @see Biome#getPrecipitation()
     * @see Biome.BiomeBuilder#precipitation(Biome.Precipitation)
     */
    void setPrecipitation(Biome.Precipitation precipitation);

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
    void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

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
