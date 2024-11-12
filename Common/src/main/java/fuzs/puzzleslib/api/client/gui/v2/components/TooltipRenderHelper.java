package fuzs.puzzleslib.api.client.gui.v2.components;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
                        TooltipFlag.Default.NORMAL
        );
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
                        TooltipFlag.Default.NORMAL
        );
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
        renderTooltipComponents(guiGraphics, posX, posY, getTooltip(itemStack), null);
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
        renderTooltipComponents(guiGraphics, posX, posY, createClientComponents(components, imageComponents), null);
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
        List<ClientTooltipComponent> clientComponents = components.stream().map(Component::getVisualOrderText).map(
                ClientTooltipComponent::create).collect(Collectors.toList());
        List<ClientTooltipComponent> clientImageComponents = imageComponents.stream().map(
                ClientAbstractions.INSTANCE::createImageComponent).toList();
        if (insertAt == -1) {
            clientComponents.addAll(clientImageComponents);
        } else {
            clientComponents.addAll(Math.min(clientComponents.size(), insertAt), clientImageComponents);
        }
        return ImmutableList.copyOf(clientComponents);
    }

    /**
     * Finally renders the tooltip, simply copied from the vanilla implementation.
     * <p>
     * Note that this method also offsets the position by +12 / -12 (x / y), just like vanilla.
     * <p>
     * Also, the tooltip is guaranteed to be placed at the specified position, no attempts at wrapping / repositioning
     * to avoid running offscreen are made.
     * <p>
     * The behavior is like using {@link DefaultTooltipPositioner#INSTANCE}.
     *
     * @param guiGraphics       the gui graphics component
     * @param posX              position on x-axis, would be mouse cursor x for vanilla
     * @param posY              position on y-axis, would be mouse cursor y for vanilla
     * @param tooltipComponents components to render in the tooltip
     */
    public static void renderTooltipComponents(GuiGraphics guiGraphics, int posX, int posY, List<? extends ClientTooltipComponent> tooltipComponents, @Nullable ResourceLocation tooltipStyle) {

        if (tooltipComponents.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        boolean isCancelled = ClientAbstractions.INSTANCE.onRenderTooltip(guiGraphics, minecraft.font, posX, posY,
                (List<ClientTooltipComponent>) tooltipComponents, DefaultTooltipPositioner.INSTANCE
        );

        if (isCancelled) return;

        int lineWidth = 0;
        int lineHeight = tooltipComponents.size() == 1 ? -2 : 0;

        for (ClientTooltipComponent component : tooltipComponents) {
            int width = component.getWidth(minecraft.font);
            if (width > lineWidth) {
                lineWidth = width;
            }
            lineHeight += component.getHeight(minecraft.font);
        }

        int originPosX = posX + 12;
        int originPosY = posY - 12;

        guiGraphics.pose().pushPose();
        guiGraphics.flush();
        TooltipRenderUtil.renderTooltipBackground(guiGraphics, originPosX, originPosY, lineWidth, lineHeight, 400,
                tooltipStyle
        );
        guiGraphics.flush();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);

        MutableInt currentPosY = new MutableInt(originPosY);
        for (int i = 0; i < tooltipComponents.size(); ++i) {
            ClientTooltipComponent component = tooltipComponents.get(i);
            guiGraphics.drawSpecial((MultiBufferSource bufferSource) -> {
                component.renderText(minecraft.font, originPosX, currentPosY.intValue(),
                        guiGraphics.pose().last().pose(), (MultiBufferSource.BufferSource) bufferSource
                );
            });
            currentPosY.add(component.getHeight(minecraft.font) + (i == 0 ? 2 : 0));
        }

        currentPosY.setValue(originPosY);
        for (int i = 0; i < tooltipComponents.size(); ++i) {
            ClientTooltipComponent component = tooltipComponents.get(i);
            component.renderImage(minecraft.font, originPosX, currentPosY.intValue(), lineWidth, lineHeight,
                    guiGraphics
            );
            currentPosY.add(component.getHeight(minecraft.font) + (i == 0 ? 2 : 0));
        }

        // we need this as opposed to the renderer in GuiGraphics, otherwise tooltip text leaks through screens
        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }
}
