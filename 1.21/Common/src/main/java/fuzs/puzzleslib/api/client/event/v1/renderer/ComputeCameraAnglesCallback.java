package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@FunctionalInterface
public interface ComputeCameraAnglesCallback {
    EventInvoker<ComputeCameraAnglesCallback> EVENT = EventInvoker.lookup(ComputeCameraAnglesCallback.class);

    /**
     * Runs before camera angle setup is done, allows for additional control over roll (which vanilla itself does not
     * support) in addition to pitch and yaw.
     *
     * @param renderer    the game renderer instance
     * @param camera      the client camera instance
     * @param partialTick partial ticks
     * @param yaw         angle for x-axis, transformation is applied second
     * @param pitch       angle for y-axis, transformation is applied last
     * @param roll        angle for z-axis, transformation is applied first
     */
    void onComputeCameraAngles(GameRenderer renderer, Camera camera, float partialTick, MutableFloat pitch, MutableFloat yaw, MutableFloat roll);
}
