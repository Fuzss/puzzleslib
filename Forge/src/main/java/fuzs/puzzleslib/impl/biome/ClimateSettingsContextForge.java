package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.ClimateSettingsContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.ClimateSettingsBuilder;

import java.util.Objects;

public class ClimateSettingsContextForge implements ClimateSettingsContext {
    private final ClimateSettingsBuilder context;

    public ClimateSettingsContextForge(ClimateSettingsBuilder context) {
        this.context = context;
    }

    @Override
    public void setPrecipitation(Biome.Precipitation precipitation) {
        Objects.requireNonNull(precipitation);
        this.context.setPrecipitation(precipitation);
    }

    @Override
    public Biome.Precipitation getPrecipitation() {
        return this.context.getPrecipitation();
    }

    @Override
    public void setTemperature(float temperature) {
        this.context.setTemperature(temperature);
    }

    @Override
    public float getTemperature() {
        return this.context.getTemperature();
    }

    @Override
    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
        Objects.requireNonNull(temperatureModifier);
        this.context.setTemperatureModifier(temperatureModifier);
    }

    @Override
    public void setDownfall(float downfall) {
        this.context.setDownfall(downfall);
    }

    @Override
    public float getDownfall() {
        return this.context.getDownfall();
    }
}
