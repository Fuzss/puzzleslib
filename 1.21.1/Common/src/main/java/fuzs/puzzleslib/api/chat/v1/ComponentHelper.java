package fuzs.puzzleslib.api.chat.v1;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/**
 * A helper class for converting various text representations.
 * <p>
 * Useful for text instances returned from {@link net.minecraft.client.StringSplitter} and
 * {@link Component#getVisualOrderText()}.
 */
@Deprecated
public class ComponentHelper {

    /**
     * Converts an instance of {@link FormattedText} to a {@link Component}.
     *
     * @param formattedText the text to convert
     * @return the new component
     */
    public static Component toComponent(FormattedText formattedText) {
        return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsComponent(formattedText);
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a {@link Component}.
     *
     * @param formattedCharSequence the text to convert
     * @return the new component
     */
    public static Component toComponent(FormattedCharSequence formattedCharSequence) {
        return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsComponent(formattedCharSequence);
    }

    /**
     * Converts an instance of {@link FormattedText} to a string which includes formatting codes supplied via configured
     * {@link net.minecraft.network.chat.Style Styles}.
     * <p>
     * This is mostly useful when working with instances where vanilla still renders raw strings, which inherently
     * support the old chat formatting system, such as in
     * {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
     *
     * @param formattedText the text to convert
     * @return the string
     */
    public static String toString(FormattedText formattedText) {
        return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsString(formattedText);
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a string which includes formatting codes supplied via
     * configured {@link net.minecraft.network.chat.Style Styles}.
     * <p>
     * This is mostly useful when working with instances where vanilla still renders raw strings, which inherently
     * support the old chat formatting system, such as in
     * {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
     *
     * @param formattedCharSequence the text to convert
     * @return the string
     */
    public static String toString(FormattedCharSequence formattedCharSequence) {
        return fuzs.puzzleslib.api.util.v1.ComponentHelper.getAsString(formattedCharSequence);
    }
}
