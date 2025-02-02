package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@FunctionalInterface
public interface ComputeFieldOfViewCallback {
    EventInvoker<ComputeFieldOfViewCallback> EVENT = EventInvoker.lookup(ComputeFieldOfViewCallback.class);

    /**
     * Runs after field of view is calculated, based on the game setting, but before in-game effects such as nausea are
     * applied.
     *
     * @param renderer    the game renderer instance
     * @param camera      the client camera instance
     * @param partialTick partial ticks
     * @param fieldOfView the field of view value
     */
    void onComputeFieldOfView(GameRenderer renderer, Camera camera, float partialTick, MutableFloat fieldOfView);
}
