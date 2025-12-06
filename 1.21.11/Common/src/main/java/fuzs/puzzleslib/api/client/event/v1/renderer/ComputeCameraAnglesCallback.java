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
     * @param gameRenderer the game renderer
     * @param camera       the client camera
     * @param partialTick  the partial tick
     * @param yaw          the x-axis angle; transformation is applied second
     * @param pitch        the y-axis angle; transformation is applied last
     * @param roll         the z-axis angle; transformation is applied first
     */
    void onComputeCameraAngles(GameRenderer gameRenderer, Camera camera, float partialTick, MutableFloat pitch, MutableFloat yaw, MutableFloat roll);
}
