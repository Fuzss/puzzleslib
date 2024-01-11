package fuzs.puzzleslib.api.chat.v1;

import fuzs.puzzleslib.impl.chat.FormattedContentSink;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/**
 * A helper class for converting various text representations used by vanilla to {@link Component}.
 * <p>Useful for text instances returned from {@link net.minecraft.client.StringSplitter} and {@link Component#getVisualOrderText()}.
 */
public class ComponentHelper {

    /**
     * Converts an instance of {@link FormattedText} to a {@link Component}.
     *
     * @param formattedText the text to convert
     * @return the new component
     */
    public static Component toComponent(FormattedText formattedText) {
        return new FormattedContentSink(formattedText).getComponent();
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a {@link Component}.
     *
     * @param formattedCharSequence the text to convert
     * @return the new component
     */
    public static Component toComponent(FormattedCharSequence formattedCharSequence) {
        return new FormattedContentSink(formattedCharSequence).getComponent();
    }
}
