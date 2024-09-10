package fuzs.puzzleslib.impl.client.gui;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.TooltipBuilder;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TooltipBuilderImpl implements TooltipBuilder {
    final List<FormattedText> lines = new ArrayList<>();
    Duration delay = Duration.ZERO;
    int maxLineWidth;
    @Nullable
    Function<AbstractWidget, ClientTooltipPositioner> tooltipPositionerFactory;
    @Nullable
    Supplier<List<? extends FormattedText>> linesSupplier;

    public TooltipBuilderImpl() {
        this(new FormattedText[0]);
    }

    public TooltipBuilderImpl(FormattedText... lines) {
        this(Arrays.asList(lines));
    }

    public TooltipBuilderImpl(List<? extends FormattedText> lines) {
        this.lines.addAll(lines);
    }

    @Override
    public TooltipBuilder addLines(FormattedText... lines) {
        Objects.requireNonNull(lines, "lines is null");
        return this.addLines(Arrays.asList(lines));
    }

    @Override
    public TooltipBuilder addLines(List<? extends FormattedText> lines) {
        Objects.requireNonNull(lines, "lines is null");
        this.lines.addAll(lines);
        return this;
    }

    @Override
    public TooltipBuilder setLines(Supplier<List<? extends FormattedText>> supplier) {
        Objects.requireNonNull(supplier, "lines supplier is null");
        this.linesSupplier = supplier;
        return this;
    }

    @Override
    public TooltipBuilder setDelay(Duration delay) {
        Objects.requireNonNull(delay, "delay is null");
        this.delay = delay;
        return this;
    }

    @Override
    public TooltipBuilder setTooltipPositionerFactory(Function<AbstractWidget, ClientTooltipPositioner> factory) {
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
        Preconditions.checkArgument(maxWidth >= 0, "max width is negative");
        this.maxLineWidth = maxWidth;
        return this;
    }

    @Override
    public void build(AbstractWidget abstractWidget) {
        abstractWidget.tooltip = new WidgetTooltipHolderImpl(abstractWidget, this);
    }
}
