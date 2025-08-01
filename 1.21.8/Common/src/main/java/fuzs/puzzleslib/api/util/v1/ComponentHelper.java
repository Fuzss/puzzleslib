package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.chat.StyleCombiningCharSink;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A helper class for converting various text representations.
 * <p>
 * Useful for text instances returned from {@link net.minecraft.client.StringSplitter} and
 * {@link Component#getVisualOrderText()}.
 * <p>
 * All methods automatically convert legacy {@link ChatFormatting} included as part of the backing string to a proper
 * {@link Style} object.
 */
public final class ComponentHelper {

    private ComponentHelper() {
        // NO-OP
    }

    @Deprecated(forRemoval = true)
    public static Component toComponent(FormattedText formattedText) {
        return getAsComponent(formattedText);
    }

    @Deprecated(forRemoval = true)
    public static Component toComponent(FormattedCharSequence formattedCharSequence) {
        return getAsComponent(formattedCharSequence);
    }

    @Deprecated(forRemoval = true)
    public static String toString(FormattedText formattedText) {
        return getAsString(formattedText);
    }

    @Deprecated(forRemoval = true)
    public static String toString(FormattedCharSequence formattedCharSequence) {
        return getAsString(formattedCharSequence);
    }

    /**
     * Converts a string to a {@link Component}.
     *
     * @param string the string to convert
     * @return the new component
     */
    public static Component getAsComponent(String string) {
        Objects.requireNonNull(string, "string is null");
        return getAsComponent(FormattedText.of(string));
    }

    /**
     * Converts an instance of {@link FormattedText} to a {@link Component}.
     *
     * @param formattedText the text to convert
     * @return the new component
     */
    public static Component getAsComponent(FormattedText formattedText) {
        return iterate(formattedText, StyleCombiningCharSink::getAsComponent);
    }

    /**
     * Converts an instance of {@link FormattedText} to a string which includes formatting codes supplied via configured
     * {@link Style Styles}.
     * <p>
     * This is mostly useful when working with instances where vanilla still renders raw strings, which inherently
     * support the old chat formatting system, such as in
     * {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
     *
     * @param formattedText the text to convert
     * @return the string
     */
    public static String getAsString(FormattedText formattedText) {
        return iterate(formattedText, StyleCombiningCharSink::getAsString);
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a {@link Component}.
     *
     * @param formattedCharSequence the text to convert
     * @return the new component
     */
    public static Component getAsComponent(FormattedCharSequence formattedCharSequence) {
        return iterate(formattedCharSequence, StyleCombiningCharSink::getAsComponent);
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a string which includes formatting codes supplied via
     * configured {@link Style Styles}.
     * <p>
     * This is mostly useful when working with instances where vanilla still renders raw strings, which inherently
     * support the old chat formatting system, such as in
     * {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
     *
     * @param formattedCharSequence the text to convert
     * @return the string
     */
    public static String getAsString(FormattedCharSequence formattedCharSequence) {
        return iterate(formattedCharSequence, StyleCombiningCharSink::getAsString);
    }

    private static <T> T iterate(FormattedText formattedText, StyleCombiningCharSink.FormattedContentComposer<T> formattedContentComposer) {
        Objects.requireNonNull(formattedText, "formatted text is null");
        StyleCombiningCharSink styleCombiningCharSink = new StyleCombiningCharSink();
        // use this to properly convert legacy formatting codes that are part of the string value
        StringDecomposer.iterateFormatted(formattedText, Style.EMPTY, styleCombiningCharSink);
        return formattedContentComposer.apply(styleCombiningCharSink);
    }

    private static <T> T iterate(FormattedCharSequence formattedCharSequence, StyleCombiningCharSink.FormattedContentComposer<T> formattedContentComposer) {
        Objects.requireNonNull(formattedCharSequence, "formatted char sequence is null");
        StyleCombiningCharSink styleCombiningCharSink = new StyleCombiningCharSink();
        formattedCharSequence.accept(styleCombiningCharSink);
        // we have to convert to a component to be able to iterate using StringDecomposer::iterateFormatted
        return iterate(styleCombiningCharSink.getAsComponent(), formattedContentComposer);
    }

    /**
     * Gets the primary (i.e. the very first) {@link Style} used by a {@link String}.
     * <p>
     * Useful for formatting code config options together with {@link #getAsString(Style)}.
     *
     * @param string the string
     * @return the style
     */
    public static Style getDefaultStyle(String string) {
        Objects.requireNonNull(string, "string is null");
        Component component = getAsComponent(string);

        // No style will have been passed when the component is empty.
        // We could also strip formatting codes from the string and check if it is empty,
        // but that might return false negatives with surrogates, maybe.
        if (!string.isEmpty() && component.getString().isEmpty()) {
            // A hack to get raw formatting without any non-formatting characters to apply.
            // We basically check if only formatting is present (when the component is empty),
            // then insert a temporary space and create a new component from that, which we only use the style from.
            int index = string.indexOf(ChatFormatting.RESET.toString());
            StringBuilder stringBuilder = new StringBuilder(string);
            stringBuilder.insert(index != -1 ? index : string.length(), " ");
            return getAsComponent(stringBuilder.toString()).getStyle();
        } else {
            return component.getStyle();
        }
    }

    /**
     * Gets the primary (i.e. the very first) {@link Style} used by a {@link FormattedText}.
     *
     * @param formattedText the text
     * @return the style
     */
    public static Style getDefaultStyle(FormattedText formattedText) {
        // pass this through our component conversion to make sure all formatting codes included in the string itself are properly applied
        return getAsComponent(formattedText).getStyle();
    }

    /**
     * Gets the primary (i.e. the very first) {@link Style} used by a {@link FormattedCharSequence}.
     *
     * @param formattedCharSequence the text
     * @return the style
     */
    public static Style getDefaultStyle(FormattedCharSequence formattedCharSequence) {
        // pass this through our component conversion to make sure all formatting codes included in the string itself are properly applied
        return getAsComponent(formattedCharSequence).getStyle();
    }

    /**
     * Converts a {@link Style} to {@link ChatFormatting chat formatting codes} and outputs those as a single string.
     *
     * @param style the style
     * @return the string consisting of legacy formatting codes
     */
    public static String getAsString(Style style) {
        Objects.requireNonNull(style, "style is null");

        if (style.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        getLegacyFormat(style, (ChatFormatting chatFormatting) -> {
            stringBuilder.append(chatFormatting.toString());
        });

        return stringBuilder.toString();
    }

    /**
     * Outputs all {@link ChatFormatting chat formatting codes} supplied by a style in the correct order.
     *
     * @param style                  the style
     * @param chatFormattingConsumer the chat formatting output
     */
    public static void getLegacyFormat(Style style, Consumer<ChatFormatting> chatFormattingConsumer) {
        Objects.requireNonNull(style, "style is null");

        if (style.isEmpty()) {
            return;
        }

        // colour must be added before formatting
        if (style.getColor() != null) {
            ChatFormatting color = ChatFormatting.getByName(style.getColor().serialize());

            if (color != null) {
                chatFormattingConsumer.accept(color);
            }
        }

        // multiple formatting codes may exist at the same time
        if (style.isBold()) {
            chatFormattingConsumer.accept(ChatFormatting.BOLD);
        }

        if (style.isItalic()) {
            chatFormattingConsumer.accept(ChatFormatting.ITALIC);
        }

        if (style.isUnderlined()) {
            chatFormattingConsumer.accept(ChatFormatting.UNDERLINE);
        }

        if (style.isStrikethrough()) {
            chatFormattingConsumer.accept(ChatFormatting.STRIKETHROUGH);
        }

        if (style.isObfuscated()) {
            chatFormattingConsumer.accept(ChatFormatting.OBFUSCATED);
        }
    }

    /**
     * Reverts effects from using {@link Style#applyLegacyFormat(ChatFormatting)} to achieve the same result as
     * {@link Style#applyFormat(ChatFormatting)} would for a more compact and versatile style object.
     *
     * @param style the style
     * @return the sanitised style
     */
    public static Style sanitizeLegacyFormat(Style style) {
        Objects.requireNonNull(style, "style is null");

        if (style.isEmpty()) {
            return style;
        }

        if (!style.isBold()) {
            style = style.withBold(null);
        }

        if (!style.isItalic()) {
            style = style.withItalic(null);
        }

        if (!style.isUnderlined()) {
            style = style.withUnderlined(null);
        }

        if (!style.isStrikethrough()) {
            style = style.withStrikethrough(null);
        }

        if (!style.isObfuscated()) {
            style = style.withObfuscated(null);
        }

        return style;
    }
}
