package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.util.v1.HSV;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

public class DyedSpawnEggItem extends SpawnEggItem {
    private final int backgroundColor;
    private final int highlightColor;

    public DyedSpawnEggItem(EntityType<? extends Mob> defaultType, int backgroundColor, int highlightColor, Properties properties) {
        super(defaultType, properties);
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
    }

    public int backgroundColor() {
        return this.backgroundColor;
    }

    public int highlightColor() {
        return this.highlightColor;
    }

    /**
     * @author ChatGPT
     */
    public static int generateHighlightColor(int backgroundColor) {
        // Convert base color to HSB
        int hsv = HSV.rgbToHsv(ARGB.redFloat(backgroundColor),
                ARGB.greenFloat(backgroundColor),
                ARGB.blueFloat(backgroundColor));
        // Modify saturation and brightness
        float saturation = Math.min(1.0F, HSV.saturationFloat(hsv) * 1.2F);
        float value = Math.max(0.0F, HSV.valueFloat(hsv) * 0.75F);
        // Convert back to RGB
        return Mth.hsvToRgb(HSV.hueFloat(hsv), saturation, value);
    }
}
