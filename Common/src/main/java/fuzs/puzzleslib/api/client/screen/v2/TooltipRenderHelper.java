package fuzs.puzzleslib.api.client.screen.v2;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A helper class for rendering tooltips without a screen, additionally allows for rendering multiple image components whereas vanilla only supports one.
 * <p>Note that the implementation is directly copied from vanilla, all caveats apply. This is done to circumvent other mods that interfere with tooltip rendering (such as Adaptive Tooltips and ToolTipFix),
 * since they otherwise cause an implementation such as this one without a proper valid screen to fail or even crash.
 */
public final class TooltipRenderHelper extends GuiComponent {

    private TooltipRenderHelper() {

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
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, Component component, ClientTooltipComponent imageComponent) {
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
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, List<Component> components, ClientTooltipComponent imageComponent) {
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
    public static void renderTooltip(PoseStack poseStack, int posX, int posY, List<Component> components, List<ClientTooltipComponent> imageComponents) {
        renderTooltipInternal(poseStack, posX, posY, Stream.concat(components.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create), imageComponents.stream()).toList());
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
        TooltipRenderUtil.renderTooltipBackground(GuiComponent::fillGradient, matrix4f, bufferBuilder, l, m, i, j, 400);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferUploader.drawWithShader(bufferBuilder.end());
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
