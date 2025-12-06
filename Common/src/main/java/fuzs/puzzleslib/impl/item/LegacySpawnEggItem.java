package fuzs.puzzleslib.impl.item;

import fuzs.puzzleslib.api.util.v1.HSV;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.SpawnEggItem;

@Deprecated(forRemoval = true)
public class LegacySpawnEggItem extends SpawnEggItem {
    private final int backgroundColor;
    private final int highlightColor;

    public LegacySpawnEggItem(int backgroundColor, int highlightColor, Properties properties) {
        super(properties);
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getHighlightColor() {
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
