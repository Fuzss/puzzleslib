package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.ClimateSettingsContext;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record ClimateSettingsContextForge(Supplier<Biome.ClimateSettings> supplier, Consumer<Biome.ClimateSettings> consumer) implements ClimateSettingsContext {

    @Override
    public void hasPrecipitation(boolean hasPrecipitation) {
        this.consumer.accept(new Biome.ClimateSettings(hasPrecipitation ? Biome.Precipitation.RAIN : Biome.Precipitation.NONE, this.getContext().temperature, this.getContext().temperatureModifier, this.getContext().downfall));
    }

    @Override
    public boolean hasPrecipitation() {
        return this.getContext().precipitation != Biome.Precipitation.NONE;
    }

    @Override
    public void setTemperature(float temperature) {
        this.consumer.accept(new Biome.ClimateSettings(this.getContext().precipitation, temperature, this.getContext().temperatureModifier, this.getContext().downfall));
    }

    @Override
    public float getTemperature() {
        return this.getContext().temperature;
    }

    @Override
    public void setTemperatureModifier(@NotNull Biome.TemperatureModifier temperatureModifier) {
        Objects.requireNonNull(temperatureModifier, "temperature modifier is null");
        this.consumer.accept(new Biome.ClimateSettings(this.getContext().precipitation, this.getContext().temperature, temperatureModifier, this.getContext().downfall));
    }

    @Override
    public void setDownfall(float downfall) {
        this.consumer.accept(new Biome.ClimateSettings(this.getContext().precipitation, this.getContext().temperature, this.getContext().temperatureModifier, downfall));
    }

    private Biome.ClimateSettings getContext() {
        return this.supplier.get();
    }
}
