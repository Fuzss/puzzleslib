package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import net.minecraft.client.Minecraft;
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
     * Split a formatted text instance according to a max width.
     *
     * @param tooltipLines component to split for building tooltip
     * @return stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(FormattedText... tooltipLines) {
        return splitTooltipLines(Arrays.asList(tooltipLines));
    }

    /**
     * Split a formatted text instance according to a max width.
     *
     * @param tooltipLines components to split for building tooltip
     * @return stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(List<? extends FormattedText> tooltipLines) {
        return tooltipLines.stream().flatMap((FormattedText formattedText) -> {
            List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(formattedText, 170);
            if (lines.isEmpty()) {
                // empty components yield an empty list
                // since empty lines are desired on tooltips make sure they don't go missing
                return Stream.of(FormattedCharSequence.EMPTY);
            } else {
                return lines.stream();
            }
        });
    }
}
