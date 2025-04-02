package fuzs.puzzleslib.api.client.gui.v2.tooltip;

import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A small helper class for splitting instance of {@link FormattedText}. Useful for splitting tooltip components.
 */
public final class ClientComponentSplitter {

    private ClientComponentSplitter() {
        // NO-OP
    }

    /**
     * Split formatted text instances.
     *
     * @param tooltipLines the lines for building the tooltip
     * @return the split components
     */
    public static List<Component> splitTooltipComponents(FormattedText... tooltipLines) {
        return splitTooltipLines(tooltipLines).map(ComponentHelper::toComponent).toList();
    }

    /**
     * Split formatted text instances.
     *
     * @param tooltipLines the lines for building the tooltip
     * @return the split components
     */
    public static List<Component> splitTooltipComponents(List<? extends FormattedText> tooltipLines) {
        return splitTooltipLines(tooltipLines).map(ComponentHelper::toComponent).toList();
    }

    /**
     * Split formatted text instances.
     *
     * @param tooltipLines the lines for building the tooltip
     * @return the stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(FormattedText... tooltipLines) {
        return splitTooltipLines(Arrays.asList(tooltipLines));
    }

    /**
     * Split formatted text instances.
     *
     * @param tooltipLines the lines for building the tooltip
     * @return the stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(List<? extends FormattedText> tooltipLines) {
        return splitTooltipLines(170, tooltipLines);
    }

    /**
     * Split formatted text instances according to a max width.
     *
     * @param maxWidth     the maximum text width for the line splitter
     * @param tooltipLines the lines for building the tooltip
     * @return the stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(int maxWidth, FormattedText... tooltipLines) {
        return splitTooltipLines(maxWidth, Arrays.asList(tooltipLines));
    }

    /**
     * Split formatted text instances according to a max width.
     *
     * @param maxWidth     the maximum text width for the line splitter
     * @param tooltipLines the lines for building the tooltip
     * @return the stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(int maxWidth, List<? extends FormattedText> tooltipLines) {
        return tooltipLines.stream().flatMap((FormattedText formattedText) -> {
            List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(formattedText, maxWidth);
            if (lines.isEmpty()) {
                // empty components yield an empty list
                // since empty lines are desired on tooltips make sure they don't go missing
                return Stream.of(FormattedCharSequence.EMPTY);
            } else {
                return lines.stream();
            }
        });
    }

    /**
     * Process formatted text instances into char sequences.
     *
     * @param tooltipLines the lines for building the tooltip
     * @return the stream of char sequences
     */
    public static Stream<FormattedCharSequence> processTooltipLines(FormattedText... tooltipLines) {
        return processTooltipLines(Arrays.asList(tooltipLines));
    }

    /**
     * Process formatted text instances into char sequences.
     *
     * @param tooltipLines the lines for building the tooltip
     * @return the stream of char sequences
     */
    public static Stream<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
        return tooltipLines.stream().map((FormattedText formattedText) -> {
            return ClientLanguage.getInstance().getVisualOrder(formattedText);
        });
    }
}
