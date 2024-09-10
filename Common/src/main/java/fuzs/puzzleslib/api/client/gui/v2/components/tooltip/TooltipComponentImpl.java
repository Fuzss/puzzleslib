package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import com.google.common.base.Preconditions;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The base implementation of {@link TooltipComponent}. Useful for creating custom vanilla screen tooltips, designed to
 * be extensible.
 */
public class TooltipComponentImpl extends AbstractTooltipComponent implements TooltipComponent {
    private List<? extends FormattedText> lines = Collections.emptyList();

    /**
     * @param abstractWidget the widget to attach this tooltip to
     * @param lines          the internal tooltip components
     */
    public TooltipComponentImpl(AbstractWidget abstractWidget, FormattedText... lines) {
        this(abstractWidget, Arrays.asList(lines));
    }

    /**
     * @param abstractWidget the widget to attach this tooltip to
     * @param lines          the internal tooltip components
     */
    public TooltipComponentImpl(AbstractWidget abstractWidget, List<? extends FormattedText> lines) {
        this(abstractWidget);
        Objects.requireNonNull(lines, "lines is null");
        this.lines = lines;
    }

    /**
     * @param abstractWidget the widget to attach this tooltip to
     */
    public TooltipComponentImpl(AbstractWidget abstractWidget) {
        super(abstractWidget);
    }

    @Override
    public List<FormattedCharSequence> toCharSequence() {
        Language language = Language.getInstance();
        if (this.getTooltip().cachedTooltip == null || language != this.getTooltip().splitWithLanguage) {
            Preconditions.checkState(!this.lines.isEmpty(), "lines is empty");
            this.getTooltip().cachedTooltip = this.processTooltipLines(this.lines);
            this.getTooltip().splitWithLanguage = language;
        }

        return this.getTooltip().cachedTooltip;
    }

    /**
     * Process formatted text instances into char sequences.
     *
     * @param tooltipLines components for building the tooltip
     * @return list of char sequences
     */
    public List<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
        return ClientComponentSplitter.splitTooltipLines(tooltipLines).toList();
    }

    @Override
    public void refreshTooltipForNextRenderPass(AbstractWidget abstractWidget) {
        List<? extends FormattedText> lines = this.getLinesForNextRenderPass();
        Objects.requireNonNull(lines, "lines is null");
        if (!lines.isEmpty()) {
            this.setLines(lines);
        }
        if (!this.lines.isEmpty()) {
            super.refreshTooltipForNextRenderPass(abstractWidget);
        }
    }

    @Override
    public void setLines(List<? extends FormattedText> lines) {
        if (!Objects.equals(lines, this.lines)) {
            this.lines = lines;
            this.getTooltip().cachedTooltip = null;
        }
    }
}
