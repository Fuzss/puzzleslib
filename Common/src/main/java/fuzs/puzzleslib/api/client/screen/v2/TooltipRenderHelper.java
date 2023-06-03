package fuzs.puzzleslib.api.client.screen.v2;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A helper class for rendering tooltips without a screen, additionally allows for rendering multiple image components whereas vanilla only supports one.
 * <p>Note that the implementation is directly copied from vanilla, all caveats apply. This is done to circumvent other mods that interfere with tooltip rendering (such as Adaptive Tooltips and ToolTipFix),
 * since they otherwise cause an implementation such as this one without a proper valid screen to fail or even crash.
 */
public final class TooltipRenderHelper extends GuiComponent {

    private TooltipRenderHelper() {

    }

    /**
     * Creates individual tooltip lines from an item stack.
     * <p>{@link TooltipFlag} defaults to the value set in <code>options.txt</code>.
     * <p>This only includes text tooltip components, for the image component an item may provide see {@link #getTooltip}.
     *
     * @param itemStack the item stack to retrieve the tooltip from
     * @return the tooltip lines as list
     */
    public static List<Component> getTooltipLines(ItemStack itemStack) {
        return getTooltipLines(itemStack, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }

    /**
     * Creates individual tooltip lines from an item stack.
     * <p>This only includes text tooltip components, for the image component an item may provide see {@link #getTooltip}.
     *
     * @param itemStack   the item stack to retrieve the tooltip from
     * @param tooltipFlag the tooltip flag to use
     * @return the tooltip lines as list
     */
    public static List<Component> getTooltipLines(ItemStack itemStack, TooltipFlag tooltipFlag) {
        Objects.requireNonNull(itemStack, "item stack is null");
        Objects.requireNonNull(tooltipFlag, "tooltip flag is null");
        return itemStack.getTooltipLines(Minecraft.getInstance().player, tooltipFlag);
    }

    /**
     * Creates all client tooltip components for an item stack, including a possibly present image component.
     * <p>{@link TooltipFlag} defaults to the value set in <code>options.txt</code>.
     *
     * @param itemStack the item stack to retrieve the tooltip from
     * @return client tooltip components
     */
    public static List<ClientTooltipComponent> getTooltip(ItemStack itemStack) {
        return getTooltip(itemStack, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
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
     * @param poseStack the pose stack
     * @param posX      position on x-axis, would be mouse cursor x for vanilla
     * @param posY      position on y-axis, would be mouse cursor y for vanilla
     * @param itemStack the item stack to retrieve the tooltip from
     */
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "item stack is null");
        renderTooltipInternal(poseStack, posX, posY, getTooltip(itemStack));
    }

    /**
     * Renders a tooltip, accepts a single text component and a single image component.
     *
     * @param poseStack      the pose stack
     * @param posX           position on x-axis, would be mouse cursor x for vanilla
     * @param posY           position on y-axis, would be mouse cursor y for vanilla
     * @param component      component to render in the tooltip
     * @param imageComponent image component to render in the tooltip
     */
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, Component component, TooltipComponent imageComponent) {
        Objects.requireNonNull(component, "component is null");
        Objects.requireNonNull(imageComponent, "image component is null");
        renderTooltip(poseStack, posX, posY, List.of(component), imageComponent);
    }

    /**
     * Renders a tooltip, accepts text components and a single image component, just like vanilla.
     *
     * @param poseStack      the pose stack
     * @param posX           position on x-axis, would be mouse cursor x for vanilla
     * @param posY           position on y-axis, would be mouse cursor y for vanilla
     * @param components     components to render in the tooltip
     * @param imageComponent image component to render in the tooltip
     */
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, List<Component> components, TooltipComponent imageComponent) {
        Objects.requireNonNull(imageComponent, "image component is null");
        renderTooltip(poseStack, posX, posY, components, List.of(imageComponent));
    }

    /**
     * Renders a tooltip, accepts text components and does not include any image components.
     *
     * @param poseStack  the pose stack
     * @param posX       position on x-axis, would be mouse cursor x for vanilla
     * @param posY       position on y-axis, would be mouse cursor y for vanilla
     * @param components components to render in the tooltip
     */
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, List<Component> components) {
        renderTooltip(poseStack, posX, posY, components, List.of());
    }

    /**
     * Renders a tooltip, accepts text components and multiple image components.
     *
     * @param poseStack       the pose stack
     * @param posX            position on x-axis, would be mouse cursor x for vanilla
     * @param posY            position on y-axis, would be mouse cursor y for vanilla
     * @param components      components to render in the tooltip
     * @param imageComponents image components to render in the tooltip
     */
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, List<Component> components, List<TooltipComponent> imageComponents) {
        renderTooltipInternal(poseStack, posX, posY, createClientComponents(components, imageComponents));
    }

    /**
     * Creates a list of {@link ClientTooltipComponent}s from text and image components to be rendered on a tooltip.
     * <p><code>imageComponents</code> are inserted into <code>components</code> at index <code>1</code>, just like vanilla.
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
     * @param insertAt        index to insert <code>imageComponents</code> into <code>components</code>, set to <code>-1</code> to insert at the end
     * @return the client tooltip components
     */
    public static List<ClientTooltipComponent> createClientComponents(List<Component> components, List<TooltipComponent> imageComponents, int insertAt) {
        List<ClientTooltipComponent> clientComponents = components.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
        List<ClientTooltipComponent> clientImageComponents = imageComponents.stream().map(ClientAbstractions.INSTANCE::createImageComponent).toList();
        if (insertAt == -1) {
            clientComponents.addAll(clientImageComponents);
        } else {
            clientComponents.addAll(insertAt, clientImageComponents);
        }
        return ImmutableList.copyOf(clientComponents);
    }

    /**
     * Finally renders the tooltip, simply copied from the vanilla implementation.
     * <p>Note that this method also offsets the position by +12 / -12 (x / y), just like vanilla.
     * <p>Also the tooltip is guaranteed to be placed at the specified position, no attempts at wrapping / repositioning to avoid running offscreen are made.
     *
     * @param poseStack  the pose stack
     * @param posX       position on x-axis, would be mouse cursor x for vanilla
     * @param posY       position on y-axis, would be mouse cursor y for vanilla
     * @param components components to render in the tooltip
     */
    public static void renderTooltipInternal(PoseStack poseStack, int posX, int posY, List<ClientTooltipComponent> components) {

        if (components.isEmpty()) return;

        Font font = Minecraft.getInstance().font;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        int i = 0;
        int j = components.size() == 1 ? -2 : 0;

        for (ClientTooltipComponent component : components) {
            int k = component.getWidth(font);
            if (k > i) {
                i = k;
            }
            j += component.getHeight();
        }

        int l = posX + 12;
        int m = posY - 12;
        poseStack.pushPose();
        float f = itemRenderer.blitOffset;
        itemRenderer.blitOffset = 400.0F;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = poseStack.last().pose();
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + i + 3, m - 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 3, m + l + 3, l + i + 3, m + l + 4, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m + l + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + l + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l + i + 3, m - 3, l + i + 4, m + l + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + l + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, l + i + 2, m - 3 + 1, l + i + 3, m + l + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m - 3 + 1, 400, 1347420415, 1347420415);
        fillGradient(matrix4f, bufferBuilder, l - 3, m + l + 2, l + i + 3, m + l + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        poseStack.translate(0.0F, 0.0F, 400.0F);
        int p = m;

        int q;
        ClientTooltipComponent clientTooltipComponent2;
        for (q = 0; q < components.size(); ++q) {
            clientTooltipComponent2 = components.get(q);
            clientTooltipComponent2.renderText(font, l, p, matrix4f, bufferSource);
            p += clientTooltipComponent2.getHeight() + (q == 0 ? 2 : 0);
        }

        bufferSource.endBatch();
        poseStack.popPose();
        p = m;

        for (q = 0; q < components.size(); ++q) {
            clientTooltipComponent2 = (ClientTooltipComponent) components.get(q);
            clientTooltipComponent2.renderImage(font, l, p, poseStack, itemRenderer, 400);
            p += clientTooltipComponent2.getHeight() + (q == 0 ? 2 : 0);
        }

        itemRenderer.blitOffset = f;
    }
}
