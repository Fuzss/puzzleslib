package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An implementation of {@link TooltipComponent} as an abstraction on top of {@link WidgetTooltipHolder}.
 * <p>
 * For using this class check out {@link TooltipComponentImpl}.
 */
abstract class AbstractTooltipComponent extends WidgetTooltipHolder implements TooltipComponent {
    private final AbstractWidget abstractWidget;

    public AbstractTooltipComponent(AbstractWidget abstractWidget) {
        Objects.requireNonNull(abstractWidget, "abstract widget is null");
        this.abstractWidget = abstractWidget;
        abstractWidget.tooltip = this;
        super.set(new Tooltip(CommonComponents.EMPTY, null) {

            @Override
            public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
                return AbstractTooltipComponent.this.toCharSequence();
            }
        });
    }

    @Override
    public void refreshTooltipForNextRenderPass(AbstractWidget abstractWidget) {
        super.refreshTooltipForNextRenderPass(abstractWidget.isHovered(),
                abstractWidget.isFocused(),
                abstractWidget.getRectangle()
        );
    }

    @Override
    public ClientTooltipPositioner createTooltipPositioner(AbstractWidget abstractWidget) {
        return super.createTooltipPositioner(abstractWidget.getRectangle(),
                abstractWidget.isHovered(),
                abstractWidget.isFocused()
        );
    }

    @Override
    public final void setTooltipDelay(Duration duration) {
        super.setDelay(duration);
    }

    @Override
    public final Duration getTooltipDelay() {
        return this.delay;
    }

    Tooltip getTooltip() {
        // we do not allow our internal tooltip to change, so this is never null
        Tooltip tooltip = super.get();
        Objects.requireNonNull(tooltip, "tooltip is null");
        return tooltip;
    }

    @Deprecated
    @Override
    public final void setDelay(Duration delay) {
        this.setTooltipDelay(delay);
    }

    @Deprecated
    @Override
    public final void set(@Nullable Tooltip tooltip) {
        // do not allow our internal tooltip instance to change, only update the message component
        this.setLines(tooltip != null ? Collections.singletonList(tooltip.message) : Collections.emptyList());
    }

    @Deprecated
    @Override
    public final Tooltip get() {
        return this.getTooltip();
    }

    @Deprecated
    @Override
    public final void refreshTooltipForNextRenderPass(boolean hovering, boolean focused, ScreenRectangle screenRectangle) {
        this.refreshTooltipForNextRenderPass(this.abstractWidget);
    }

    @Deprecated
    @Override
    public final ClientTooltipPositioner createTooltipPositioner(ScreenRectangle screenRectangle, boolean hovering, boolean focused) {
        return this.createTooltipPositioner(this.abstractWidget);
    }

    @Deprecated
    @Override
    public final void updateNarration(NarrationElementOutput output) {
        super.updateNarration(output);
    }
}
