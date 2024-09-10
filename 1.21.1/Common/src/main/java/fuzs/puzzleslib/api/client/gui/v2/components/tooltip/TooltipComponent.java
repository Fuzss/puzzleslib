package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * An abstraction for vanilla screen tooltips.
 */
@ApiStatus.NonExtendable
public interface TooltipComponent {

    /**
     * Convert the internal tooltip components to char sequences, usually via
     * {@link net.minecraft.client.gui.Font#split(FormattedText, int)}.
     *
     * @return the char sequences
     */
    List<FormattedCharSequence> toCharSequence();

    /**
     * Called after rendering the widget this tooltip is attached to. Updates the internal tooltip components, and
     * passes the tooltip for rendering if necessary,
     *
     * @param abstractWidget the widget this tooltip is attached to, useful for retrieving e.g.
     *                       {@link AbstractWidget#isHovered()}, {@link AbstractWidget#isFocused()} and
     *                       {@link AbstractWidget#getRectangle()}
     */
    void refreshTooltipForNextRenderPass(AbstractWidget abstractWidget);

    /**
     * @param abstractWidget the widget this tooltip is attached to, useful for retrieving e.g.
     *                       {@link AbstractWidget#isHovered()}, {@link AbstractWidget#isFocused()} and
     *                       {@link AbstractWidget#getRectangle()}
     * @return the positioner for providing tooltip coordinates on the screen
     */
    ClientTooltipPositioner createTooltipPositioner(AbstractWidget abstractWidget);

    /**
     * Sets the internal tooltip components. Providing an empty list will prevent the tooltip from showing.
     * <p>
     * Internal tooltip components will be overwritten if {@link #getLinesForNextRenderPass()} returns a non-empty list.
     *
     * @param lines the new tooltip components
     */
    void setLines(List<? extends FormattedText> lines);

    /**
     * @param duration delay after which the tooltip shows
     */
    void setTooltipDelay(Duration duration);

    /**
     * @return delay after which the tooltip shows
     */
    Duration getTooltipDelay();

    /**
     * Allows for dynamically updating tooltip components. Returning an empty list will prevent the contents from
     * changing.
     *
     * @return the new tooltip components
     */
    default List<? extends FormattedText> getLinesForNextRenderPass() {
        return Collections.emptyList();
    }
}
