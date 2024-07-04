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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * A small helper class for creating new instances of {@link Tooltip} from multiple
 * {@link net.minecraft.network.chat.Component} instances instead of just a single one, as is the case in the vanilla
 * implementation.
 */
public final class ScreenTooltipFactory {
    public static final int MAX_TOOLTIP_WIDTH = 170;

    private ScreenTooltipFactory() {
        // NO-OP
    }

    /**
     * Split a formatted text instance according to a max width.
     *
     * @param tooltipLine component to split for building tooltip
     * @return stream of split char sequences
     */
    public static Stream<FormattedCharSequence> splitToCharSequence(FormattedText tooltipLine) {
        List<FormattedCharSequence> splitLines = Minecraft.getInstance().font.split(tooltipLine, MAX_TOOLTIP_WIDTH);
        if (splitLines.isEmpty()) {
            // empty components yield an empty list
            // since empty lines are desired on tooltips make sure they don't go missing
            return Stream.of(FormattedCharSequence.EMPTY);
        } else {
            return splitLines.stream();
        }
    }

    /**
     * Split a formatted text instance according to a max width.
     *
     * @param tooltipLines components to split for building tooltip
     * @return stream of split char sequences
     */
    public static List<FormattedCharSequence> splitToCharSequence(@Nullable List<? extends FormattedText> tooltipLines) {
        if (tooltipLines != null) {
            return tooltipLines.stream().flatMap(ScreenTooltipFactory::splitToCharSequence).toList();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param tooltipLines components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip createFromText(FormattedText... tooltipLines) {
        return createFromText(Arrays.asList(tooltipLines));
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param tooltipLines components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip createFromText(List<? extends FormattedText> tooltipLines) {
        return createFromCharSequence(splitToCharSequence(tooltipLines));
    }

    /**
     * Create a new tooltip instance from already split lines of text.
     *
     * @param tooltipLines      the split lines to build the tooltip from
     * @param positionerFactory factory to replace
     *                          {@link Tooltip#createTooltipPositioner(boolean, boolean, ScreenRectangle)}, the boolean
     *                          parameter for the factory defines where vanilla would return
     *                          {@link
     *                          net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner}
     *                          instead of
     *                          {@link net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner}
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
     * Set some text as a new tooltip instance for an {@link AbstractWidget}.
     *
     * @param abstractWidget    the widget
     * @param tooltipLines      the lines to build the tooltip from
     * @return the original widget
     */
    public static AbstractWidget setWidgetTooltipFromText(AbstractWidget abstractWidget, FormattedText... tooltipLines) {
        return setWidgetTooltipFromCharSequence(abstractWidget, splitToCharSequence(Arrays.asList(tooltipLines)));
    }

    /**
     * Set some text as a new tooltip instance for an {@link AbstractWidget}.
     *
     * @param abstractWidget    the widget
     * @param tooltipLines      the lines to build the tooltip from
     * @return the original widget
     */
    public static AbstractWidget setWidgetTooltipFromText(AbstractWidget abstractWidget, @Nullable List<? extends FormattedText> tooltipLines) {
        return setWidgetTooltipFromCharSequence(abstractWidget, splitToCharSequence(tooltipLines));
    }

    /**
     * Set some text as a new tooltip instance for an {@link AbstractWidget}.
     *
     * @param abstractWidget    the widget
     * @param tooltipLines      the lines to build the tooltip from
     * @return the original widget
     */
    public static AbstractWidget setWidgetTooltipFromCharSequence(AbstractWidget abstractWidget, @Nullable List<FormattedCharSequence> tooltipLines) {
        return setWidgetTooltipFromCharSequence(abstractWidget, tooltipLines, null);
    }

    /**
     * Set some text as a new tooltip instance for an {@link AbstractWidget}.
     *
     * @param abstractWidget    the widget
     * @param tooltipLines      the lines to build the tooltip from
     * @param positionerFactory factory for creating the tooltip positioner
     * @return the original widget
     */
    public static AbstractWidget setWidgetTooltipFromCharSequence(AbstractWidget abstractWidget, @Nullable List<FormattedCharSequence> tooltipLines, @Nullable BiFunction<ScreenRectangle, Boolean, ClientTooltipPositioner> positionerFactory) {

        if (positionerFactory != null) {
            Objects.requireNonNull(tooltipLines, "tooltip lines is null");
            WidgetTooltipHolder holder = createTooltipHolder(tooltipLines, positionerFactory);
            holder.setDelay(abstractWidget.tooltip.delay);
            abstractWidget.tooltip = holder;
        } else if (tooltipLines != null) {
            abstractWidget.setTooltip(createFromCharSequence(tooltipLines));
        } else {
            abstractWidget.setTooltip(null);
        }

        return abstractWidget;
    }

    private static WidgetTooltipHolder createTooltipHolder(List<FormattedCharSequence> tooltipLines, BiFunction<ScreenRectangle, Boolean, ClientTooltipPositioner> positionerFactory) {
        // Keep this internal, as the class is not available before Minecraft 1.20.5.
        Objects.requireNonNull(positionerFactory, "positioner factory is null");
        WidgetTooltipHolder holder = new WidgetTooltipHolder() {

            @Override
            protected ClientTooltipPositioner createTooltipPositioner(ScreenRectangle screenRectangle, boolean hovering, boolean focused) {
                return positionerFactory.apply(screenRectangle,
                        !hovering && focused && Minecraft.getInstance().getLastInputType().isKeyboard()
                );
            }
        };
        holder.set(createFromCharSequence(tooltipLines));
        return holder;
    }
}
