package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.ClimateSettingsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class ClimateSettingsContextFabric implements ClimateSettingsContext {
    private final Biome biome;
    private final BiomeModificationContext.WeatherContext context;

    public ClimateSettingsContextFabric(Biome biome, BiomeModificationContext.WeatherContext context) {
        this.biome = biome;
        this.context = context;
    }

    @Override
    public void setPrecipitation(@NotNull Biome.Precipitation precipitation) {
        this.context.setPrecipitation(precipitation);
    }

    @Override
    public Biome.Precipitation getPrecipitation() {
        return this.biome.getPrecipitation();
    }

    @Override
    public void setTemperature(float temperature) {
        this.context.setTemperature(temperature);
    }

    @Override
    public float getTemperature() {
        return this.biome.getBaseTemperature();
    }

    @Override
    public void setTemperatureModifier(@NotNull Biome.TemperatureModifier temperatureModifier) {
        this.context.setTemperatureModifier(temperatureModifier);
    }

    @Override
    public void setDownfall(float downfall) {
        this.context.setDownfall(downfall);
    }

    @Override
    public float getDownfall() {
        return this.biome.getDownfall();
    }
}
