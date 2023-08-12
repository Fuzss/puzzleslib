package fuzs.puzzleslib.api.client.screen.v2;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.ScreenOpeningCallback;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A helper class for replicating deferred tooltip rendering introduced in Minecraft 1.19.3.
 */
public final class DeferredTooltipRendering {
    private static final Map<AbstractWidget, List<FormattedCharSequence>> WIDGET_TOOLTIPS = new MapMaker().weakKeys().makeMap();
    @Nullable
    private static List<FormattedCharSequence> tooltip;

    private DeferredTooltipRendering() {

    }

    /**
     * Set a tooltip to be rendered at the end of the next {@link Screen#render(PoseStack, int, int, float)} call.
     *
     * @param minecraft minecraft singleton to get {@link net.minecraft.client.gui.Font} instance from
     * @param tooltip   the tooltip lines
     */
    public static void setTooltipForNextRenderPass(Minecraft minecraft, Component tooltip) {
        setTooltipForNextRenderPass(splitTooltip(minecraft, tooltip));
    }

    /**
     * Set a tooltip to be rendered at the end of the next {@link Screen#render(PoseStack, int, int, float)} call.
     *
     * @param tooltip the tooltip lines
     */
    public static void setTooltipForNextRenderPass(List<FormattedCharSequence> tooltip) {
        setTooltipForNextRenderPass(tooltip, true);
    }

    /**
     * Set a tooltip to be rendered at the end of the next {@link Screen#render(PoseStack, int, int, float)} call.
     *
     * @param tooltip  the tooltip lines
     * @param override override a potentially present tooltip
     */
    public static void setTooltipForNextRenderPass(List<FormattedCharSequence> tooltip, boolean override) {
        if (DeferredTooltipRendering.tooltip == null || override) {
            DeferredTooltipRendering.tooltip = tooltip;
        }
    }

    /**
     * Splits a {@link Component} into multiple lines with a max width of <code>170</code> pixels.
     *
     * @param minecraft minecraft singleton to get {@link net.minecraft.client.gui.Font} instance from
     * @param tooltip   the tooltip lines to split
     * @return the split lines as a list
     */
    public static List<FormattedCharSequence> splitTooltip(Minecraft minecraft, FormattedText... tooltip) {
        return splitTooltip(minecraft, Arrays.asList(tooltip));
    }

    /**
     * Splits a {@link Component} into multiple lines with a max width of <code>170</code> pixels.
     *
     * @param minecraft minecraft singleton to get {@link net.minecraft.client.gui.Font} instance from
     * @param tooltip   the tooltip lines to split
     * @return the split lines as a list
     */
    public static List<FormattedCharSequence> splitTooltip(Minecraft minecraft, List<? extends FormattedText> tooltip) {
        return tooltip.stream().flatMap(t -> minecraft.font.split(t, 170).stream()).toList();
    }

    /**
     * Set a tooltip to a widget to be rendered when the widget is hovered or focused.
     *
     * @param minecraft      minecraft singleton to get {@link net.minecraft.client.gui.Font} instance from
     * @param abstractWidget the widget that shows the tooltip
     * @param tooltip        the tooltip lines to split
     */
    public static void setTooltipForNextRenderPass(Minecraft minecraft, AbstractWidget abstractWidget, FormattedText... tooltip) {
        setTooltip(abstractWidget, splitTooltip(minecraft, tooltip));
    }

    /**
     * Set a tooltip to a widget to be rendered when the widget is hovered or focused.
     *
     * @param abstractWidget the widget that shows the tooltip
     * @param tooltip        the tooltip lines to split
     */
    public static void setTooltipForNextRenderPass(AbstractWidget abstractWidget, List<? extends FormattedText> tooltip) {
        setTooltip(abstractWidget, splitTooltip(Minecraft.getInstance(), tooltip));
    }

    /**
     * Set a tooltip to a widget to be rendered when the widget is hovered or focused.
     * <p>In contrast to vanilla widget tooltip rendering in 1.18.2 the widget must only be visible and not necessarily active.
     *
     * @param abstractWidget the widget that shows the tooltip
     * @param tooltip        the tooltip lines, set to <code>null</code> to remove any previously set tooltip
     */
    public static void setTooltip(AbstractWidget abstractWidget, @Nullable List<FormattedCharSequence> tooltip) {
        if (tooltip != null) {
            WIDGET_TOOLTIPS.put(abstractWidget, tooltip);
        } else {
            WIDGET_TOOLTIPS.remove(abstractWidget);
        }
    }

    @ApiStatus.Internal
    public static void registerHandlers() {
        // can't do this in a static block as first screen to load this class has already been initialized and events won't trigger until a screen is opened or initialized
        // only an issue on Fabric where those events are registered during screen initialization
        ScreenEvents.afterRender(Screen.class).register((screen, poseStack, mouseX, mouseY, tickDelta) -> {
            screen.children().forEach(DeferredTooltipRendering::updateTooltip);
            if (tooltip != null) {
                screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
                tooltip = null;
            }
        });
        ScreenOpeningCallback.EVENT.register((oldScreen, newScreen) -> {
            tooltip = null;
            return EventResult.PASS;
        });
        ScreenEvents.remove(Screen.class).register(screen -> tooltip = null);
    }

    private static void updateTooltip(GuiEventListener guiEventListener) {
        if (guiEventListener instanceof ContainerEventHandler containerEventHandler) {
            containerEventHandler.children().forEach(DeferredTooltipRendering::updateTooltip);
        } else if (guiEventListener instanceof AbstractWidget abstractWidget && abstractWidget.visible && abstractWidget.isHoveredOrFocused()) {
            List<FormattedCharSequence> tooltip = WIDGET_TOOLTIPS.get(abstractWidget);
            if (tooltip != null) setTooltipForNextRenderPass(tooltip, abstractWidget.isFocused());
        }
    }
}
