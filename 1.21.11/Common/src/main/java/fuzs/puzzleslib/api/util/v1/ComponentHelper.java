package fuzs.puzzleslib.api.util.v1;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

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

    /**
     * Converts a string to a {@link Component}.
     *
     * @param text the string to convert
     * @return the new component
     */
    public static Component getAsComponent(String text) {
        return StyleCombiningCharSink.of(text, Style.EMPTY).getAsComponent();
    }

    /**
     * Converts an instance of {@link FormattedText} to a {@link Component}.
     *
     * @param formattedText the text to convert
     * @return the new component
     */
    public static Component getAsComponent(FormattedText formattedText) {
        return StyleCombiningCharSink.of(formattedText, Style.EMPTY).getAsComponent();
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
        return StyleCombiningCharSink.of(formattedText, Style.EMPTY).getAsString();
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a {@link Component}.
     *
     * @param formattedCharSequence the text to convert
     * @return the new component
     */
    public static Component getAsComponent(FormattedCharSequence formattedCharSequence) {
        return StyleCombiningCharSink.of(formattedCharSequence, Style.EMPTY).getAsComponent();
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
        return StyleCombiningCharSink.of(formattedCharSequence, Style.EMPTY).getAsString();
    }

    /**
     * Converts a string to a {@link Component} with the specified {@link Style}.
     *
     * @param text  the string to convert
     * @param style the style to apply to the component
     * @return the new component with the applied style
     */
    public static Component getAsComponent(String text, Style style) {
        return Component.literal(text).withStyle(style);
    }

    /**
     * Converts the given string into a formatted string by applying the specified {@link Style}.
     * <p>
     * If the provided style is not empty, the method appends the style's formatting codes and resets formatting after
     * the original string.
     *
     * @param text  the original string to format
     * @param style the {@link Style} to apply to the string
     * @return the formatted string if the style is not empty; otherwise, the original string
     */
    public static String getAsString(String text, Style style) {
        if (!style.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ComponentHelper.getAsString(style));
            stringBuilder.append(text);
            stringBuilder.append(ChatFormatting.RESET);
            return stringBuilder.toString();
        } else {
            return text;
        }
    }

    /**
     * Gets the primary (i.e. the very first) {@link Style} used by a {@link String}.
     * <p>
     * Useful for formatting code config options together with {@link #getAsString(Style)}.
     *
     * @param text the string
     * @return the style
     */
    public static Style getDefaultStyle(String text) {
        Objects.requireNonNull(text, "text is null");
        Component component = getAsComponent(text);
        // No style will have been passed when the component is empty.
        // We could also strip formatting codes from the string and check if it is empty,
        // but that might return false negatives with surrogates, maybe.
        if (!text.isEmpty() && component.getString().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(text);
            // A hack to get raw formatting without any non-formatting characters to apply.
            // We basically check if only formatting is present (when the component is empty),
            // then insert a temporary space and create a new component from that, which we only use the style from.
            int index = text.indexOf(ChatFormatting.RESET.toString());
            stringBuilder.insert(index != -1 ? index : text.length(), " ");
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
