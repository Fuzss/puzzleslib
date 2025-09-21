package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

@FunctionalInterface
public interface RenderLevelCallback {
    EventInvoker<Terrain> TERRAIN = EventInvoker.lookup(Terrain.class);
    EventInvoker<Entities> ENTITIES = EventInvoker.lookup(Entities.class);
    EventInvoker<Translucent> TRANSLUCENT = EventInvoker.lookup(Translucent.class);
    EventInvoker<All> ALL = EventInvoker.lookup(All.class);

    /**
     * @param levelRenderer the level renderer
     * @param camera        the camera
     * @param gameRenderer  the game renderer
     * @param deltaTracker  the delta tracker
     * @param poseStack     the pose stack
     * @param frustum       the frustum
     * @param clientLevel   the client level
     */
    void onRenderLevel(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, DeltaTracker deltaTracker, PoseStack poseStack, Frustum frustum, ClientLevel clientLevel);

    /**
     * Fires after all solid block render types have been rendered in a level.
     */
    @FunctionalInterface
    interface Terrain extends RenderLevelCallback {

    }

    /**
     * Fires after entities have been rendered in a level.
     */
    @FunctionalInterface
    interface Entities extends RenderLevelCallback {

    }

    /**
     * Fires after translucent objects have been rendered in a level.
     */
    @FunctionalInterface
    interface Translucent extends RenderLevelCallback {

    }

    /**
     * Fires after a level has been fully rendered.
     */
    @FunctionalInterface
    interface All extends RenderLevelCallback {

    }
}
