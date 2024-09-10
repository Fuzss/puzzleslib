package fuzs.puzzleslib.impl.client.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.TooltipBuilder;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.TooltipComponentImpl;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TooltipBuilderImpl implements TooltipBuilder {
    private final List<FormattedText> lines = new ArrayList<>();
    @Nullable
    private Duration delay;
    private int maxLineWidth;
    @Nullable
    private Function<AbstractWidget, ClientTooltipPositioner> tooltipPositionerFactory;
    @Nullable
    private Supplier<List<? extends FormattedText>> linesSupplier;

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
        this.maxLineWidth = maxWidth;
        return this;
    }

    @Override
    public void build(AbstractWidget abstractWidget) {
        Preconditions.checkState(!this.lines.isEmpty() || this.linesSupplier != null, "lines is empty");
        new TooltipComponentImpl(abstractWidget, ImmutableList.copyOf(this.lines)) {

            {
                if (TooltipBuilderImpl.this.delay != null) {
                    this.setTooltipDelay(TooltipBuilderImpl.this.delay);
                }
            }

            @Override
            public List<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
                if (TooltipBuilderImpl.this.maxLineWidth != 0) {
                    return ClientComponentSplitter.splitTooltipLines(TooltipBuilderImpl.this.maxLineWidth, tooltipLines).toList();
                } else {
                    return ClientComponentSplitter.processTooltipLines(tooltipLines).toList();
                }
            }

            @Override
            public List<? extends FormattedText> getLinesForNextRenderPass() {
                if (TooltipBuilderImpl.this.linesSupplier != null) {
                    return TooltipBuilderImpl.this.linesSupplier.get();
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public ClientTooltipPositioner createTooltipPositioner(AbstractWidget abstractWidget) {
                if (TooltipBuilderImpl.this.tooltipPositionerFactory != null) {
                    return TooltipBuilderImpl.this.tooltipPositionerFactory.apply(abstractWidget);
                } else {
                    return super.createTooltipPositioner(abstractWidget);
                }
            }
        };
    }
}
