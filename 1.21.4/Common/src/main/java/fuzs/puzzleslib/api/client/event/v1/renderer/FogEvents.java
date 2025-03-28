package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.shaders.FogShape;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;

public final class FogEvents {
    public static final EventInvoker<ComputeColor> COMPUTE_COLOR = EventInvoker.lookup(ComputeColor.class);
    public static final EventInvoker<Render> RENDER = EventInvoker.lookup(Render.class);

    private FogEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface ComputeColor {

        /**
         * Called after the fog color is calculated from the current block overlay or biome. Allows for modifying the
         * fog color.
         *
         * @param gameRenderer the game renderer instance
         * @param camera       the camera instance
         * @param partialTick  the partial tick
         * @param fogRed       the red color component
         * @param fogGreen     the green color component
         * @param fogBlue      the blue color component
         */
        void onComputeFogColor(GameRenderer gameRenderer, Camera camera, float partialTick, MutableFloat fogRed, MutableFloat fogGreen, MutableFloat fogBlue);
    }

    @FunctionalInterface
    public interface Render {

        /**
         * Called before fog is rendered, allows for controlling fog start and end distance.
         *
         * @param gameRenderer the game renderer instance
         * @param camera       the camera instance
         * @param partialTick  the partial tick
         * @param fogMode      the fog mode
         * @param fogType      the fog type
         * @param fogStart     the distance from the camera for the fog to start
         * @param fogEnd       the distance from the camera for the fog to end
         * @param fogShape     the spherical or cylindrical fog shape
         */
        void onRenderFog(GameRenderer gameRenderer, Camera camera, float partialTick, FogRenderer.FogMode fogMode, FogType fogType, MutableFloat fogStart, MutableFloat fogEnd, MutableValue<FogShape> fogShape);
    }
}
