package fuzs.puzzleslib.api.client.gui.v2.tooltip;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A helper class for rendering tooltips without a screen, additionally allows for rendering multiple image components
 * whereas vanilla only supports one.
 * <p>
 * Note that the implementation is directly copied from vanilla, all caveats apply. This is done to circumvent other
 * mods that interfere with tooltip rendering (such as Adaptive Tooltips and ToolTipFix), since they otherwise cause an
 * implementation such as this one without a proper valid screen to fail or even crash.
 */
public final class TooltipRenderHelper {

    private TooltipRenderHelper() {
        // NO-OP
    }

    /**
     * Creates individual tooltip lines from an item stack.
     * <p>{@link TooltipFlag} defaults to the value set in <code>options.txt</code>.
     * <p>This only includes text tooltip components, for the image component an item may provide see
     * {@link #getTooltip}.
     *
     * @param itemStack the item stack to retrieve the tooltip from
     * @return the tooltip lines as list
     */
    public static List<Component> getTooltipLines(ItemStack itemStack) {
        return getTooltipLines(itemStack,
                Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED :
                        TooltipFlag.Default.NORMAL);
    }

    /**
     * Creates individual tooltip lines from an item stack.
     * <p>This only includes text tooltip components, for the image component an item may provide see
     * {@link #getTooltip}.
     *
     * @param itemStack   the item stack to retrieve the tooltip from
     * @param tooltipFlag the tooltip flag to use
     * @return the tooltip lines as list
     */
    public static List<Component> getTooltipLines(ItemStack itemStack, TooltipFlag tooltipFlag) {
        Objects.requireNonNull(itemStack, "item stack is null");
        Objects.requireNonNull(tooltipFlag, "tooltip flag is null");
        Minecraft minecraft = Minecraft.getInstance();
        return itemStack.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, tooltipFlag);
    }

    /**
     * Creates all client tooltip components for an item stack, including a possibly present image component.
     * <p>{@link TooltipFlag} defaults to the value set in <code>options.txt</code>.
     *
     * @param itemStack the item stack to retrieve the tooltip from
     * @return client tooltip components
     */
    public static List<ClientTooltipComponent> getTooltip(ItemStack itemStack) {
        return getTooltip(itemStack,
                Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED :
                        TooltipFlag.Default.NORMAL);
    }

    /**
     * Creates all client tooltip components for an item stack, including a possibly present image component.
     *
     * @param itemStack   the item stack to retrieve the tooltip from
     * @param tooltipFlag the tooltip flag to use
     * @return client tooltip components
     */
    public static List<ClientTooltipComponent> getTooltip(ItemStack itemStack, TooltipFlag tooltipFlag) {
        Objects.requireNonNull(itemStack, "item stack is null");
        Objects.requireNonNull(tooltipFlag, "tooltip flag is null");
        List<Component> components = getTooltipLines(itemStack, tooltipFlag);
        List<TooltipComponent> imageComponents = itemStack.getTooltipImage().map(List::of).orElse(List.of());
        return createClientComponents(components, imageComponents);
    }

    /**
     * Renders a tooltip, accepts an item stack.
     *
     * @param guiGraphics the gui graphics component
     * @param posX        position on x-axis, would be mouse cursor x for vanilla
     * @param posY        position on y-axis, would be mouse cursor y for vanilla
     * @param itemStack   the item stack to retrieve the tooltip from
     */
    public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "item stack is null");
        renderTooltipComponents(guiGraphics, posX, posY, getTooltip(itemStack));
    }

    /**
     * Renders a tooltip, accepts a single text component and a single image component.
     *
     * @param guiGraphics    the gui graphics component
     * @param posX           position on x-axis, would be mouse cursor x for vanilla
     * @param posY           position on y-axis, would be mouse cursor y for vanilla
     * @param component      component to render in the tooltip
     * @param imageComponent image component to render in the tooltip
     */
    public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, Component component, TooltipComponent imageComponent) {
        Objects.requireNonNull(component, "component is null");
        Objects.requireNonNull(imageComponent, "image component is null");
        renderTooltip(guiGraphics, posX, posY, List.of(component), imageComponent);
    }

    /**
     * Renders a tooltip, accepts text components and a single image component, just like vanilla.
     *
     * @param guiGraphics    the gui graphics component
     * @param posX           position on x-axis, would be mouse cursor x for vanilla
     * @param posY           position on y-axis, would be mouse cursor y for vanilla
     * @param components     components to render in the tooltip
     * @param imageComponent image component to render in the tooltip
     */
    public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, List<Component> components, TooltipComponent imageComponent) {
        Objects.requireNonNull(imageComponent, "image component is null");
        renderTooltip(guiGraphics, posX, posY, components, List.of(imageComponent));
    }

    /**
     * Renders a tooltip, accepts text components and does not include any image components.
     *
     * @param guiGraphics the gui graphics component
     * @param posX        position on x-axis, would be mouse cursor x for vanilla
     * @param posY        position on y-axis, would be mouse cursor y for vanilla
     * @param components  components to render in the tooltip
     */
    public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, List<Component> components) {
        renderTooltip(guiGraphics, posX, posY, components, List.of());
    }

    /**
     * Renders a tooltip, accepts text components and multiple image components.
     *
     * @param guiGraphics     the gui graphics component
     * @param posX            position on x-axis, would be mouse cursor x for vanilla
     * @param posY            position on y-axis, would be mouse cursor y for vanilla
     * @param components      components to render in the tooltip
     * @param imageComponents image components to render in the tooltip
     */
    public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, List<Component> components, List<TooltipComponent> imageComponents) {
        renderTooltipComponents(guiGraphics, posX, posY, createClientComponents(components, imageComponents));
    }

    /**
     * Creates a list of {@link ClientTooltipComponent}s from text and image components to be rendered on a tooltip.
     * <p><code>imageComponents</code> are inserted into <code>components</code> at index <code>1</code>, just like
     * vanilla.
     *
     * @param components      components to render on the tooltip
     * @param imageComponents image components to render on the tooltip
     * @return the client tooltip components
     */
    public static List<ClientTooltipComponent> createClientComponents(List<Component> components, List<TooltipComponent> imageComponents) {
        return createClientComponents(components, imageComponents, 1);
    }

    /**
     * Creates a list of {@link ClientTooltipComponent}s from text and image components to be rendered on a tooltip.
     *
     * @param components      components to render on the tooltip
     * @param imageComponents image components to render on the tooltip
     * @param insertAt        index to insert <code>imageComponents</code> into <code>components</code>, set to
     *                        <code>-1</code> to insert at the end
     * @return the client tooltip components
     */
    public static List<ClientTooltipComponent> createClientComponents(List<Component> components, List<TooltipComponent> imageComponents, int insertAt) {
        List<ClientTooltipComponent> clientComponents = components.stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .collect(Collectors.toList());
        List<ClientTooltipComponent> clientImageComponents = imageComponents.stream()
                .map(TooltipRenderHelper::createImageComponent)
                .toList();
        if (insertAt == -1) {
            clientComponents.addAll(clientImageComponents);
        } else {
            clientComponents.addAll(Math.min(clientComponents.size(), insertAt), clientImageComponents);
        }
        return ImmutableList.copyOf(clientComponents);
    }

    /**
     * Converts an image {@link TooltipComponent} into the appropriate client-side component.
     * <p>
     * {@link ClientTooltipComponent}s must first be registered in
     * {@link ClientModConstructor#onRegisterClientTooltipComponents(ClientTooltipComponentsContext)}, otherwise
     * {@link IllegalArgumentException} will be thrown.
     * <p>
     * For simple text-based components directly use {@link ClientTooltipComponent#create(FormattedCharSequence)}
     * instead.
     *
     * @param imageComponent the un-sided {@link TooltipComponent} to convert
     * @return the client tooltip components representation
     */
    public static ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        return ClientProxyImpl.get().createImageComponent(imageComponent);
    }

    /**
     * Finally, renders the tooltip, simply copied from the vanilla implementation.
     * <p>
     * Note that this method also offsets the position by +12 / -12 (x / y), just like vanilla.
     * <p>
     * Also, the tooltip is guaranteed to be placed at the specified position, no attempts at wrapping / repositioning
     * to avoid running offscreen are made.
     * <p>
     * The behaviour is like using {@link DefaultTooltipPositioner#INSTANCE}.
     *
     * @param guiGraphics       the gui graphics component
     * @param posX              the position on x-axis; would be mouse cursor x for vanilla
     * @param posY              the position on y-axis; would be mouse cursor y for vanilla
     * @param tooltipComponents components to render in the tooltip
     * @see GuiGraphics#renderTooltip(Font, List, int, int, ClientTooltipPositioner, ResourceLocation)
     */
    public static void renderTooltipComponents(GuiGraphics guiGraphics, int posX, int posY, List<? extends ClientTooltipComponent> tooltipComponents) {
        renderTooltipComponents(guiGraphics, posX, posY, tooltipComponents, null);
    }

    /**
     * Finally, renders the tooltip, simply copied from the vanilla implementation.
     * <p>
     * Note that this method also offsets the position by +12 / -12 (x / y), just like vanilla.
     * <p>
     * Also, the tooltip is guaranteed to be placed at the specified position, no attempts at wrapping / repositioning
     * to avoid running offscreen are made.
     * <p>
     * The behaviour is like using {@link DefaultTooltipPositioner#INSTANCE}.
     *
     * @param guiGraphics       the gui graphics component
     * @param posX              the position on x-axis; would be mouse cursor x for vanilla
     * @param posY              the position on y-axis; would be mouse cursor y for vanilla
     * @param tooltipComponents the components to render in the tooltip
     * @param tooltipStyle      the optional resource location for overriding the background and frame sprites of the
     *                          tooltip
     * @see GuiGraphics#renderTooltip(Font, List, int, int, ClientTooltipPositioner, ResourceLocation)
     */
    public static void renderTooltipComponents(GuiGraphics guiGraphics, int posX, int posY, List<? extends ClientTooltipComponent> tooltipComponents, @Nullable ResourceLocation tooltipStyle) {

        Font font = Minecraft.getInstance().font;

        if (tooltipComponents.isEmpty() || onRenderTooltip(guiGraphics,
                font,
                posX,
                posY,
                (List<ClientTooltipComponent>) tooltipComponents,
                DefaultTooltipPositioner.INSTANCE)) {
            return;
        }

        int lineWidth = 0;
        int lineHeight = tooltipComponents.size() == 1 ? -2 : 0;

        for (ClientTooltipComponent component : tooltipComponents) {
            int width = component.getWidth(font);
            if (width > lineWidth) {
                lineWidth = width;
            }
            lineHeight += component.getHeight(font);
        }

        int originPosX = posX + 12;
        int originPosY = posY - 12;

        guiGraphics.pose().pushMatrix();
        TooltipRenderUtil.renderTooltipBackground(guiGraphics,
                originPosX,
                originPosY,
                lineWidth,
                lineHeight,
                tooltipStyle);

        MutableInt currentPosY = new MutableInt(originPosY);
        for (int i = 0; i < tooltipComponents.size(); ++i) {
            ClientTooltipComponent component = tooltipComponents.get(i);
            component.renderText(guiGraphics, font, originPosX, currentPosY.intValue());
            currentPosY.add(component.getHeight(font) + (i == 0 ? 2 : 0));
        }

        currentPosY.setValue(originPosY);
        for (int i = 0; i < tooltipComponents.size(); ++i) {
            ClientTooltipComponent component = tooltipComponents.get(i);
            component.renderImage(font, originPosX, currentPosY.intValue(), lineWidth, lineHeight, guiGraphics);
            currentPosY.add(component.getHeight(font) + (i == 0 ? 2 : 0));
        }

        guiGraphics.pose().popMatrix();
    }

    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     *
     * @param guiGraphics the gui graphics instance
     * @param font        the font instance
     * @param mouseX      the x-position of the mouse cursor
     * @param mouseY      the y-position of the mouse cursor
     * @param components  the components rendering in the tooltip
     * @param positioner  the positioner for placing the tooltip in relation to provided mouse coordinates
     * @return <code>true</code> to prevent the tooltip from rendering, allows for fully taking over rendering
     */
    public static boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        return ClientProxyImpl.get().onRenderTooltip(guiGraphics, font, mouseX, mouseY, components, positioner);
    }
}
