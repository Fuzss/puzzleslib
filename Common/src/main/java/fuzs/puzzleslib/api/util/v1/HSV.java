package fuzs.puzzleslib.api.util.v1;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Helper class for packing and unpacking hsv color components to and from an integer.
 *
 * @see ARGB
 */
public class HSV {

    /**
     * @param color the packed color
     * @return the unpacked hue component
     */
    public static int hue(int color) {
        return color >> 16 & 0xFF;
    }

    /**
     * @param color the packed color
     * @return the unpacked saturation component
     */
    public static int saturation(int color) {
        return color >> 8 & 0xFF;
    }

    /**
     * @param color the packed color
     * @return the unpacked value / brightness component
     */
    public static int value(int color) {
        return color & 0xFF;
    }

    /**
     * @param hue        the unpacked hue component
     * @param saturation the unpacked saturation component
     * @param value      the unpacked value / brightness component
     * @return the packed color
     */
    public static int color(int hue, int saturation, int value) {
        return hue << 16 | saturation << 8 | value;
    }

    /**
     * @param hue        the unpacked hue component
     * @param saturation the unpacked saturation component
     * @param value      the unpacked value / brightness component
     * @return the packed color
     */
    public static int colorFromFloat(float hue, float saturation, float value) {
        return color(ARGB.as8BitChannel(hue), ARGB.as8BitChannel(saturation), ARGB.as8BitChannel(value));
    }

    /**
     * @param color the packed color
     * @return the unpacked hue component
     */
    public static float hueFloat(int color) {
        return ARGB.from8BitChannel(hue(color));
    }

    /**
     * @param color the packed color
     * @return the unpacked saturation component
     */
    public static float saturationFloat(int color) {
        return ARGB.from8BitChannel(saturation(color));
    }

    /**
     * @param color the packed color
     * @return the unpacked value / brightness component
     */
    public static float valueFloat(int color) {
        return ARGB.from8BitChannel(value(color));
    }

    /**
     * @param color the packed rgb color
     * @return the packed hsv color
     */
    public static int rgbToHsv(int color) {
        return rgbToHsv(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color));
    }

    /**
     * @param red   the unpacked red component
     * @param green the unpacked green component
     * @param blue  the unpacked blue component
     * @return the packed hsv color
     *
     * @author ChatGPT
     */
    public static int rgbToHsv(float red, float green, float blue) {
        // Find max and min values among R, G, B
        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        float delta = max - min;
        // Calculate Hue (H), scaled to [0,1]
        float hue = 0.0F;
        if (delta != 0.0F) {
            if (max == red) {
                hue = (green - blue) / delta % 6.0F;
            } else if (max == green) {
                hue = (blue - red) / delta + 2.0F;
            } else {
                hue = (red - green) / delta + 4.0F;
            }
            hue = hue / 6.0F; // Convert range from [0,360] to [0,1]
            if (hue < 0.0F) hue += 1.0F; // Ensure hue is non-negative
        }
        // Calculate Saturation (S)
        float saturation = max == 0.0F ? 0.0F : delta / max;
        // Calculate Value (V) - Also called Brightness (B)
        float value = max;
        // Return the HSV values as an array
        return colorFromFloat(hue, saturation, value);
    }

    /**
     * @param color the packed hsv color
     * @return the packed rgb color
     */
    public static int hsvToRgb(int color) {
        return hsvToRgb(hueFloat(color), saturationFloat(color), valueFloat(color));
    }

    /**
     * @param hue        the unpacked hue component
     * @param saturation the unpacked saturation component
     * @param value      the unpacked value / brightness component
     * @return the packed rgb color
     */
    public static int hsvToRgb(float hue, float saturation, float value) {
        return Mth.hsvToRgb(hue, saturation, value);
    }
}

