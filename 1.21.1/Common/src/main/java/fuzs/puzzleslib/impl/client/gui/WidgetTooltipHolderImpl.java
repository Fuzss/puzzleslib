package fuzs.puzzleslib.impl.client.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.ClientComponentSplitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class WidgetTooltipHolderImpl extends WidgetTooltipHolder {
    private final AbstractWidget abstractWidget;
    private List<? extends FormattedText> lines;
    private final int maxLineWidth;
    @Nullable
    private final BiFunction<ClientTooltipPositioner, AbstractWidget, ClientTooltipPositioner> tooltipPositionerFactory;
    @Nullable
    private final Supplier<List<? extends FormattedText>> linesSupplier;

    public WidgetTooltipHolderImpl(AbstractWidget abstractWidget, TooltipBuilderImpl builder) {
        Preconditions.checkState(!builder.lines.isEmpty() || builder.linesSupplier != null, "lines is empty");
        this.abstractWidget = abstractWidget;
        this.lines = builder.linesSupplier != null ? Collections.emptyList() : ImmutableList.copyOf(builder.lines);
        this.maxLineWidth = builder.maxLineWidth;
        this.tooltipPositionerFactory = builder.tooltipPositionerFactory;
        this.linesSupplier = builder.linesSupplier;
        super.setDelay(builder.delay);
        super.set(new Tooltip(CommonComponents.EMPTY, null) {

            @Override
            public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
                Language language = Language.getInstance();
                if (this.cachedTooltip == null || language != this.splitWithLanguage) {
                    this.cachedTooltip = this.processTooltipLines(WidgetTooltipHolderImpl.this.lines);
                    this.splitWithLanguage = language;
                }

                return this.cachedTooltip;
            }

            private List<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
                Preconditions.checkState(!tooltipLines.isEmpty(), "lines is empty");
                if (WidgetTooltipHolderImpl.this.maxLineWidth != 0) {
                    return ClientComponentSplitter.splitTooltipLines(WidgetTooltipHolderImpl.this.maxLineWidth, tooltipLines)
                            .toList();
                } else {
                    return ClientComponentSplitter.processTooltipLines(tooltipLines).toList();
                }
            }
        });
    }

    @Override
    public void setDelay(Duration delay) {
        // NO-OP
    }

    @Override
    public void set(@Nullable Tooltip tooltip) {
        // NO-OP
    }

    @NotNull
    @Override
    public Tooltip get() {
        Tooltip tooltip = super.get();
        Objects.requireNonNull(tooltip, "tooltip is null");
        return tooltip;
    }

    @Override
    public void refreshTooltipForNextRenderPass(boolean hovering, boolean focused, ScreenRectangle screenRectangle) {
        this.refreshLines(this.getLinesForNextRenderPass());
        Preconditions.checkState(!this.lines.isEmpty(), "lines is empty");
        super.refreshTooltipForNextRenderPass(hovering, focused, screenRectangle);
    }

    private List<? extends FormattedText> getLinesForNextRenderPass() {
        if (this.linesSupplier != null) {
            return this.linesSupplier.get();
        } else {
            return Collections.emptyList();
        }
    }

    private void refreshLines(List<? extends FormattedText> lines) {
        if (!lines.isEmpty() && !Objects.equals(lines, this.lines)) {
            this.lines = lines;
            this.get().cachedTooltip = null;
        }
    }

    @Override
    protected ClientTooltipPositioner createTooltipPositioner(ScreenRectangle screenRectangle, boolean hovering, boolean focused) {
        ClientTooltipPositioner tooltipPositioner = super.createTooltipPositioner(screenRectangle, hovering, focused);
        if (this.tooltipPositionerFactory != null) {
            return this.tooltipPositionerFactory.apply(tooltipPositioner, this.abstractWidget);
        } else {
            return tooltipPositioner;
        }
    }
}
