package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import fuzs.puzzleslib.impl.client.gui.TooltipBuilderImpl;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A builder implementation for handling {@link net.minecraft.client.gui.components.Tooltip} with some extras.
 */
public interface TooltipBuilder {

    /**
     * Creates a new builder instance.
     *
     * @return the builder instance
     */
    static TooltipBuilder create() {
        return new TooltipBuilderImpl();
    }

    /**
     * Creates a new builder instance.
     *
     * @param lines the tooltip lines
     * @return the builder instance
     */
    static TooltipBuilder create(FormattedText... lines) {
        return new TooltipBuilderImpl(lines);
    }

    /**
     * Creates a new builder instance.
     *
     * @param lines the tooltip lines
     * @return the builder instance
     */
    static TooltipBuilder create(List<? extends FormattedText> lines) {
        return new TooltipBuilderImpl(lines);
    }

    /**
     * Adds text lines to the tooltip.
     *
     * @param lines the tooltip lines
     * @return the builder instance
     */
    TooltipBuilder addLines(FormattedText... lines);

    /**
     * Adds text lines to the tooltip.
     *
     * @param lines the tooltip lines
     * @return the builder instance
     */
    TooltipBuilder addLines(List<? extends FormattedText> lines);

    /**
     * Set text lines for the tooltip.
     * <p>
     * This allows for dynamically updating the tooltip.
     * <p>
     * Lines are cached internally if they do not change from invoking the supplier.
     *
     * @param supplier the tooltip lines
     * @return the builder instance
     */
    TooltipBuilder setLines(Supplier<List<? extends FormattedText>> supplier);

    /**
     * Set a custom delay it takes for the tooltip to appear.
     *
     * @param delay the delay
     * @return the builder instance
     */
    TooltipBuilder setDelay(Duration delay);

    /**
     * Set a custom factory for positioning the tooltip around a widget.
     * <p>
     * Usually behaves differently depening on if the tooltip was triggered by the widget being hovered by the cursor,
     * or after becoming selected from navigating via keyboards keys.
     *
     * @param factory the factory
     * @return the builder instance
     */
    TooltipBuilder setTooltipPositionerFactory(Function<AbstractWidget, ClientTooltipPositioner> factory);

    /**
     * Split text lines on the tooltip.
     *
     * @return the builder instance
     */
    TooltipBuilder splitLines();

    /**
     * Split text lines on the tooltip by the specified width.
     *
     * @param maxWidth the width to split lines at
     * @return the builder instance
     */
    TooltipBuilder splitLines(int maxWidth);

    /**
     * Builds the tooltip instance and attaches it to a widget.
     * <p>
     * Can be called multiple times for different widgets.
     *
     * @param abstractWidget the widget to attach the built tooltip to
     */
    void build(AbstractWidget abstractWidget);
}
