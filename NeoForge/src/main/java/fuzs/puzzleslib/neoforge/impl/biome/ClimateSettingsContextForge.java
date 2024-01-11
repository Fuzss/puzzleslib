package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.api.biome.v1.ClimateSettingsContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.ClimateSettingsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ClimateSettingsContextForge(ClimateSettingsBuilder context) implements ClimateSettingsContext {

    @Override
    public void hasPrecipitation(boolean hasPrecipitation) {
        this.context.setHasPrecipitation(hasPrecipitation);
    }

    @Override
    public boolean hasPrecipitation() {
        return this.context.hasPrecipitation();
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
    public void setTemperatureModifier(@NotNull Biome.TemperatureModifier temperatureModifier) {
        Objects.requireNonNull(temperatureModifier, "temperature modifier is null");
        this.context.setTemperatureModifier(temperatureModifier);
    }

    @Override
    public void setDownfall(float downfall) {
        this.context.setDownfall(downfall);
    }
}
