package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;

@FunctionalInterface
public interface RenderGuiCallback {
    EventInvoker<RenderGuiCallback> EVENT = EventInvoker.lookup(RenderGuiCallback.class);

    /**
     * Called at the end of {@link net.minecraft.client.gui.Gui#render(PoseStack, float)} after vanilla has drawn all elements.
     * <p>Allows for rendering additional elements on the screen.
     *
     * @param minecraft    minecraft singleton instance
     * @param poseStack    the current pose stack
     * @param tickDelta    partial tick time
     * @param screenWidth  window width
     * @param screenHeight window height
     */
    void onRenderGui(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight);
}
