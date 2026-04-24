package fuzs.puzzleslib.common.api.client.event.v1.renderer;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.data.MutableFloat;
import net.minecraft.client.Camera;

@FunctionalInterface
public interface ComputeCameraAnglesCallback {
    EventInvoker<ComputeCameraAnglesCallback> EVENT = EventInvoker.lookup(ComputeCameraAnglesCallback.class);

    /**
     * Runs before camera angle setup is done, allows for additional control over roll (which vanilla itself does not
     * support) in addition to pitch and yaw.
     *
     * @param camera      the client camera
     * @param partialTick the partial tick
     * @param pitch       the y-axis angle; transformation is applied last
     * @param yaw         the x-axis angle; transformation is applied second
     * @param roll        the z-axis angle; transformation is applied first
     */
    void onComputeCameraAngles(Camera camera, float partialTick, MutableFloat pitch, MutableFloat yaw, MutableFloat roll);
}
