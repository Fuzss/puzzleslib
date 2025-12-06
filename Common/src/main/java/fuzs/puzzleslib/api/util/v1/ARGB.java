package fuzs.puzzleslib.api.util.v1;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * @see net.minecraft.util.FastColor.ARGB32
 */
public final class ARGB {

    private ARGB() {
        // NO-OP
    }

    public static int alpha(int color) {
        return FastColor.ARGB32.alpha(color);
    }

    public static int red(int color) {
        return FastColor.ARGB32.red(color);
    }

    public static int green(int color) {
        return FastColor.ARGB32.green(color);
    }

    public static int blue(int color) {
        return FastColor.ARGB32.blue(color);
    }

    public static int color(int alpha, int red, int green, int blue) {
        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    public static int color(int red, int green, int blue) {
        return FastColor.ARGB32.color(red, green, blue);
    }

    public static int color(Vec3 color) {
        return color(as8BitChannel((float) color.x()),
                as8BitChannel((float) color.y()),
                as8BitChannel((float) color.z()));
    }

    public static int multiply(int color1, int color2) {
        if (color1 == -1) {
            return color2;
        } else if (color2 == -1) {
            return color1;
        } else {
            return FastColor.ARGB32.multiply(color1, color2);
        }
    }

    public static int scaleRGB(int color, float scale) {
        return scaleRGB(color, scale, scale, scale);
    }

    public static int scaleRGB(int color, float redScale, float greenScale, float blueScale) {
        return color(alpha(color),
                Math.clamp((int) (red(color) * redScale), 0, 255),
                Math.clamp((int) (green(color) * greenScale), 0, 255),
                Math.clamp((int) (blue(color) * blueScale), 0, 255));
    }

    public static int scaleRGB(int color, int scale) {
        return color(alpha(color),
                Math.clamp((long) red(color) * scale / 255L, 0, 255),
                Math.clamp((long) green(color) * scale / 255L, 0, 255),
                Math.clamp((long) blue(color) * scale / 255L, 0, 255));
    }

    public static int greyscale(int color) {
        int i = (int) (red(color) * 0.3F + green(color) * 0.59F + blue(color) * 0.11F);
        return color(i, i, i);
    }

    public static int lerp(float delta, int color1, int color2) {
        return FastColor.ARGB32.lerp(delta, color1, color2);
    }

    public static int opaque(int color) {
        return FastColor.ARGB32.opaque(color);
    }

    public static int transparent(int color) {
        return color & 16777215;
    }

    public static int color(int alpha, int color) {
        return FastColor.ARGB32.color(alpha, color);
    }

    public static int color(float alpha, int color) {
        return as8BitChannel(alpha) << 24 | color & 16777215;
    }

    public static int white(float alpha) {
        return as8BitChannel(alpha) << 24 | 16777215;
    }

    public static int colorFromFloat(float alpha, float red, float green, float blue) {
        return FastColor.ARGB32.colorFromFloat(alpha, red, green, blue);
    }

    public static Vector3f vector3fFromRGB24(int color) {
        float f = red(color) / 255.0F;
        float g = green(color) / 255.0F;
        float h = blue(color) / 255.0F;
        return new Vector3f(f, g, h);
    }

    public static int average(int color1, int color2) {
        return FastColor.ARGB32.average(color1, color2);
    }

    public static int as8BitChannel(float value) {
        return Mth.floor(value * 255.0F);
    }

    public static float alphaFloat(int color) {
        return from8BitChannel(alpha(color));
    }

    public static float redFloat(int color) {
        return from8BitChannel(red(color));
    }

    public static float greenFloat(int color) {
        return from8BitChannel(green(color));
    }

    public static float blueFloat(int color) {
        return from8BitChannel(blue(color));
    }

    public static float from8BitChannel(int value) {
        return value / 255.0F;
    }

    public static int toABGR(int color) {
        return color & -16711936 | (color & 0xFF0000) >> 16 | (color & 0xFF) << 16;
    }

    public static int fromABGR(int color) {
        return toABGR(color);
    }
}
