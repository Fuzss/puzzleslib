package fuzs.puzzleslib.impl.client.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
import java.util.function.Function;
import java.util.function.Supplier;

public final class WidgetTooltipHolderImpl extends WidgetTooltipHolder {
    private final AbstractWidget abstractWidget;
    private List<? extends FormattedText> tooltipLines;
    @Nullable
    private final BiFunction<ClientTooltipPositioner, AbstractWidget, ClientTooltipPositioner> tooltipPositionerFactory;
    private final Function<List<? extends FormattedText>, List<FormattedCharSequence>> tooltipLineProcessor;
    @Nullable
    private final Supplier<List<? extends FormattedText>> tooltipLinesSupplier;

    public WidgetTooltipHolderImpl(AbstractWidget abstractWidget, TooltipBuilderImpl builder) {
        Preconditions.checkState(!builder.tooltipLines.isEmpty() || builder.tooltipLinesSupplier != null, "lines is empty");
        this.abstractWidget = abstractWidget;
        this.tooltipLines = builder.tooltipLinesSupplier != null ? Collections.emptyList() : ImmutableList.copyOf(builder.tooltipLines);
        this.tooltipLineProcessor = builder.tooltipLineProcessor;
        this.tooltipPositionerFactory = builder.tooltipPositionerFactory;
        this.tooltipLinesSupplier = builder.tooltipLinesSupplier;
        super.setDelay(builder.tooltipDelay);
        super.set(new Tooltip(CommonComponents.EMPTY, null) {

            @Override
            public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
                Language language = Language.getInstance();
                if (this.cachedTooltip == null || language != this.splitWithLanguage) {
                    this.cachedTooltip = this.processTooltipLines(WidgetTooltipHolderImpl.this.tooltipLines);
                    this.splitWithLanguage = language;
                }

                return this.cachedTooltip;
            }

            private List<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
                Preconditions.checkState(!tooltipLines.isEmpty(), "lines is empty");
                return WidgetTooltipHolderImpl.this.tooltipLineProcessor.apply(tooltipLines);
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
        Preconditions.checkState(!this.tooltipLines.isEmpty(), "lines is empty");
        super.refreshTooltipForNextRenderPass(hovering, focused, screenRectangle);
    }

    private List<? extends FormattedText> getLinesForNextRenderPass() {
        if (this.tooltipLinesSupplier != null) {
            return this.tooltipLinesSupplier.get();
        } else {
            return Collections.emptyList();
        }
    }

    private void refreshLines(List<? extends FormattedText> lines) {
        if (!lines.isEmpty() && !Objects.equals(lines, this.tooltipLines)) {
            this.tooltipLines = lines;
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
