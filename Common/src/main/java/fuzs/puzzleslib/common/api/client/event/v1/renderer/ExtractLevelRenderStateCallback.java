package fuzs.puzzleslib.common.api.client.event.v1.renderer;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;

@FunctionalInterface
public interface ExtractLevelRenderStateCallback {
    EventInvoker<ExtractLevelRenderStateCallback> EVENT = EventInvoker.lookup(ExtractLevelRenderStateCallback.class);

    /**
     * Called during {@link LevelExtractor#extract(DeltaTracker, Camera, float)}, for setting up the render state of the
     * level for future rendering.
     *
     * @param levelExtractor the level extractor
     * @param renderState    the level render state
     * @param level          the level
     * @param camera         the camera
     * @param frustum        the frustum
     * @param deltaTracker   the delta tracker
     */
    void onExtractLevelRenderState(LevelExtractor levelExtractor, LevelRenderState renderState, ClientLevel level, Camera camera, Frustum frustum, DeltaTracker deltaTracker);
}
