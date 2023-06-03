package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.ClimateSettingsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record ClimateSettingsContextFabric(Biome biome, BiomeModificationContext.WeatherContext context) implements ClimateSettingsContext {

    @Override
    public void hasPrecipitation(boolean hasPrecipitation) {
        this.context.setPrecipitation(Biome.Precipitation.RAIN);
    }

    @Override
    public boolean hasPrecipitation() {
        return this.biome.getPrecipitation() != Biome.Precipitation.NONE;
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
}
