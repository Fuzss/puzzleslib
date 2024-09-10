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
import org.joml.Matrix4f;

public final class RenderLevelEvents {
    public static final EventInvoker<AfterTerrain> AFTER_TERRAIN = EventInvoker.lookup(AfterTerrain.class);
    public static final EventInvoker<AfterEntities> AFTER_ENTITIES = EventInvoker.lookup(AfterEntities.class);
    public static final EventInvoker<AfterTranslucent> AFTER_TRANSLUCENT = EventInvoker.lookup(AfterTranslucent.class);
    public static final EventInvoker<AfterLevel> AFTER_LEVEL = EventInvoker.lookup(AfterLevel.class);

    private RenderLevelEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface AfterTerrain {

        /**
         * Fires after all three solid block render types ({@link RenderType#solid()},
         * {@link RenderType#cutoutMipped()}, and {@link RenderType#cutout()}) have been rendered in a level.
         *
         * @param levelRenderer    the level renderer instance
         * @param camera           the camera instance
         * @param gameRenderer     the game renderer instance
         * @param deltaTracker     partial tick time
         * @param poseStack        current pose stack
         * @param projectionMatrix the projection matrix from the pose stack
         * @param frustum          frustum instance
         * @param level            the current client level
         */
        void onRenderLevelAfterTerrain(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, DeltaTracker deltaTracker, PoseStack poseStack, Matrix4f projectionMatrix, Frustum frustum, ClientLevel level);
    }

    @FunctionalInterface
    public interface AfterEntities {

        /**
         * Fires after entities have been rendered in a level.
         *
         * @param levelRenderer    the level renderer instance
         * @param camera           the camera instance
         * @param gameRenderer     the game renderer instance
         * @param deltaTracker     partial tick time
         * @param poseStack        current pose stack
         * @param projectionMatrix the projection matrix from the pose stack
         * @param frustum          frustum instance
         * @param level            the current client level
         */
        void onRenderLevelAfterEntities(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, DeltaTracker deltaTracker, PoseStack poseStack, Matrix4f projectionMatrix, Frustum frustum, ClientLevel level);
    }

    @FunctionalInterface
    public interface AfterTranslucent {

        /**
         * Fires after translucent objects ({@link RenderType#translucent()}, {@link RenderType#tripwire()}, and
         * particles) have been rendered in a level.
         *
         * @param levelRenderer    the level renderer instance
         * @param camera           the camera instance
         * @param gameRenderer     the game renderer instance
         * @param deltaTracker     partial tick time
         * @param poseStack        current pose stack
         * @param projectionMatrix the projection matrix from the pose stack
         * @param frustum          frustum instance
         * @param level            the current client level
         */
        void onRenderLevelAfterTranslucent(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, DeltaTracker deltaTracker, PoseStack poseStack, Matrix4f projectionMatrix, Frustum frustum, ClientLevel level);
    }

    @FunctionalInterface
    public interface AfterLevel {

        /**
         * Fires after a level has been fully rendered.
         *
         * @param levelRenderer    the level renderer instance
         * @param camera           the camera instance
         * @param gameRenderer     the game renderer instance
         * @param deltaTracker     partial tick time
         * @param poseStack        current pose stack
         * @param projectionMatrix the projection matrix from the pose stack
         * @param frustum          frustum instance
         * @param level            the current client level
         */
        void onRenderLevelAfterLevel(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, DeltaTracker deltaTracker, PoseStack poseStack, Matrix4f projectionMatrix, Frustum frustum, ClientLevel level);
    }
}
