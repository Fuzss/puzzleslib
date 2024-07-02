package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

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
     * @param text components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip createFromText(FormattedText... text) {
        return createFromText(Arrays.asList(text));
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param text components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip createFromText(List<? extends FormattedText> text) {
        List<FormattedCharSequence> tooltipLines = text.stream().flatMap(ScreenTooltipFactory::splitText).toList();
        return createFromCharSequence(tooltipLines);
    }

    /**
     * Split a formatted text instance according to a max width.
     *
     * @param text component to split for building tooltip
     * @return stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitText(FormattedText text) {
        List<FormattedCharSequence> splitLines = Minecraft.getInstance().font.split(text, 170);
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
    public static Tooltip createFromCharSequence(List<FormattedCharSequence> tooltipLines) {
        return new Tooltip(CommonComponents.EMPTY, null) {

            @Override
            public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
                return tooltipLines;
            }
        };
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
    public static WidgetTooltipHolder create(List<FormattedCharSequence> tooltipLines, BiFunction<ScreenRectangle, Boolean, ClientTooltipPositioner> positionerFactory) {
        WidgetTooltipHolder widgetTooltipHolder = new WidgetTooltipHolder() {

            @Override
            protected ClientTooltipPositioner createTooltipPositioner(ScreenRectangle screenRectangle, boolean hovering, boolean focused) {
                if (positionerFactory != null) {
                    return positionerFactory.apply(screenRectangle,
                            !hovering && focused && Minecraft.getInstance().getLastInputType().isKeyboard()
                    );
                } else {
                    return super.createTooltipPositioner(screenRectangle, hovering, focused);
                }
            }
        };
        widgetTooltipHolder.set(createFromCharSequence(tooltipLines));
        return widgetTooltipHolder;
    }

    public static void setWidgetTooltip(AbstractWidget abstractWidget, List<FormattedCharSequence> tooltipLines, BiFunction<ScreenRectangle, Boolean, ClientTooltipPositioner> positionerFactory) {
        WidgetTooltipHolder tooltip = create(tooltipLines, positionerFactory);
        tooltip.setDelay(abstractWidget.tooltip.delay);
        abstractWidget.tooltip = tooltip;
    }
}
