package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@FunctionalInterface
public interface ExtractLevelRenderStateCallback {
    EventInvoker<ExtractLevelRenderStateCallback> EVENT = EventInvoker.lookup(ExtractLevelRenderStateCallback.class);

    /**
     * Called during
     * {@link LevelRenderer#renderLevel(GraphicsResourceAllocator, DeltaTracker, boolean, Camera, Matrix4f, Matrix4f,
     * Matrix4f, GpuBufferSlice, Vector4f, boolean)}, for setting up the render state of the level for future
     * rendering.
     *
     * @param levelRenderer the level renderer
     * @param renderState   the level render state
     * @param level         the level
     * @param camera        the camera
     * @param frustum       the frustum
     * @param deltaTracker  the delta tracker
     */
    void onExtractLevelRenderState(LevelRenderer levelRenderer, LevelRenderState renderState, ClientLevel level, Camera camera, Frustum frustum, DeltaTracker deltaTracker);
}
