package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;

@FunctionalInterface
public interface RenderLevelCallback {
    EventInvoker<AfterTerrain> AFTER_TERRAIN = EventInvoker.lookup(AfterTerrain.class);
    EventInvoker<AfterEntities> AFTER_ENTITIES = EventInvoker.lookup(AfterEntities.class);
    EventInvoker<AfterTranslucent> AFTER_TRANSLUCENT = EventInvoker.lookup(AfterTranslucent.class);
    EventInvoker<AfterAll> AFTER_ALL = EventInvoker.lookup(AfterAll.class);

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
     * Fires after all three solid block render types ({@link RenderType#solid()}, {@link RenderType#cutoutMipped()},
     * and {@link RenderType#cutout()}) have been rendered in a level.
     */
    @FunctionalInterface
    interface AfterTerrain extends RenderLevelCallback {

    }

    /**
     * Fires after entities have been rendered in a level.
     */
    @FunctionalInterface
    interface AfterEntities extends RenderLevelCallback {

    }

    /**
     * Fires after translucent objects ({@link RenderType#translucent()}, {@link RenderType#tripwire()}, and particles)
     * have been rendered in a level.
     */
    @FunctionalInterface
    interface AfterTranslucent extends RenderLevelCallback {

    }

    /**
     * Fires after a level has been fully rendered.
     */
    @FunctionalInterface
    interface AfterAll extends RenderLevelCallback {

    }
}
