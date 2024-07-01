package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * A small helper class for creating new instances of {@link Tooltip} from multiple {@link net.minecraft.network.chat.Component} instances instead of just a single one,
 * as is the case in the vanilla implementation.
 */
public final class ScreenTooltipFactory {

    private ScreenTooltipFactory() {
        // NO-OP
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param components components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(FormattedText... components) {
        return create(Arrays.asList(components));
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param formattedTexts components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(List<? extends FormattedText> formattedTexts) {
        List<FormattedCharSequence> tooltipLines = formattedTexts.stream().flatMap(ScreenTooltipFactory::splitTooltipLines).toList();
        return create(tooltipLines, null);
    }

    /**
     * Split a formatted text instance according to a max width.
     *
     * @param formattedText component to split for building tooltip
     * @return stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitTooltipLines(FormattedText formattedText) {
        List<FormattedCharSequence> splitLines = Minecraft.getInstance().font.split(formattedText, 170);
        if (splitLines.isEmpty()) {
            // empty components yield an empty list
            // since empty lines are desired on tooltips make sure they don't go missing
            return Stream.of(FormattedCharSequence.EMPTY);
        } else {
            return splitLines.stream();
        }
    }

    /**
     * Create a new tooltip instance from already split lines of text.
     *
     * @param tooltipLines      the split lines to build the tooltip from
     * @param positionerFactory factory to replace {@link Tooltip#createTooltipPositioner(boolean, boolean, ScreenRectangle)},
     *                          the boolean parameter for the factory defines where vanilla would return {@link net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner}
     *                          instead of {@link net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner}
     * @return the tooltip instance
     */
    public static Tooltip create(List<FormattedCharSequence> tooltipLines, @Nullable BiFunction<ScreenRectangle, Boolean, ClientTooltipPositioner> positionerFactory) {
        return new Tooltip(CommonComponents.EMPTY, null) {

            @Override
            public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
                return tooltipLines;
            }

            @Override
            protected ClientTooltipPositioner createTooltipPositioner(boolean hovering, boolean focused, ScreenRectangle screenRectangle) {
                if (positionerFactory != null) {
                    return positionerFactory.apply(screenRectangle, !hovering && focused && Minecraft.getInstance().getLastInputType().isKeyboard());
                } else {
                    return super.createTooltipPositioner(hovering, focused, screenRectangle);
                }
            }
        };
    }
}
