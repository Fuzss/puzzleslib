package fuzs.puzzleslib.impl.client.gui;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.TooltipBuilder;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TooltipBuilderImpl implements TooltipBuilder {
    final List<FormattedText> tooltipLines = new ArrayList<>();
    /**
     * Negative default delay, as vanilla only checks if time passed is greater, but not equal. This guarantees tooltips
     * display instantly.
     */
    Duration tooltipDelay = Duration.ofMillis(-1L);
    @Nullable BiFunction<ClientTooltipPositioner, AbstractWidget, ClientTooltipPositioner> tooltipPositionerFactory;
    Function<List<? extends FormattedText>, List<FormattedCharSequence>> tooltipLineProcessor = (List<? extends FormattedText> tooltipLines) -> {
        return ClientComponentSplitter.processTooltipLines(tooltipLines).toList();
    };
    @Nullable Supplier<List<? extends FormattedText>> tooltipLinesSupplier;

    public TooltipBuilderImpl() {
        this(new FormattedText[0]);
    }

    public TooltipBuilderImpl(FormattedText... lines) {
        this(Arrays.asList(lines));
    }

    public TooltipBuilderImpl(List<? extends FormattedText> lines) {
        this.tooltipLines.addAll(lines);
    }

    @Override
    public TooltipBuilder addLines(FormattedText... lines) {
        Objects.requireNonNull(lines, "tooltip lines is null");
        return this.addLines(Arrays.asList(lines));
    }

    @Override
    public TooltipBuilder addLines(List<? extends FormattedText> lines) {
        Objects.requireNonNull(lines, "tooltip lines is null");
        this.tooltipLines.addAll(lines);
        return this;
    }

    @Override
    public TooltipBuilder setLines(Supplier<List<? extends FormattedText>> supplier) {
        Objects.requireNonNull(supplier, "tooltip lines supplier is null");
        this.tooltipLinesSupplier = supplier;
        return this;
    }

    @Override
    public TooltipBuilder setDelay(Duration delay) {
        Objects.requireNonNull(delay, "tooltip delay is null");
        this.tooltipDelay = delay;
        return this;
    }

    @Override
    public TooltipBuilder setTooltipPositionerFactory(BiFunction<ClientTooltipPositioner, AbstractWidget, ClientTooltipPositioner> factory) {
        Objects.requireNonNull(factory, "tooltip positioner factory is null");
        this.tooltipPositionerFactory = factory;
        return this;
    }

    @Override
    public TooltipBuilder splitLines() {
        return this.splitLines(175);
    }

    @Override
    public TooltipBuilder splitLines(int maxWidth) {
        Preconditions.checkArgument(maxWidth >= 0, "max line width is negative");
        return this.setTooltipLineProcessor((List<? extends FormattedText> tooltipLines) -> {
            return ClientComponentSplitter.splitTooltipLines(maxWidth, tooltipLines).toList();
        });
    }

    @Override
    public TooltipBuilder setTooltipLineProcessor(Function<List<? extends FormattedText>, List<FormattedCharSequence>> processor) {
        Objects.requireNonNull(processor, "tooltip line processor is null");
        this.tooltipLineProcessor = processor;
        return this;
    }

    @Override
    public void build(AbstractWidget abstractWidget) {
        abstractWidget.tooltip = new WidgetTooltipHolderImpl(abstractWidget, this);
    }
}
