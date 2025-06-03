package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;

public final class FogEvents {
    public static final EventInvoker<Color> COLOR = EventInvoker.lookup(Color.class);
    public static final EventInvoker<Setup> SETUP = EventInvoker.lookup(Setup.class);

    private FogEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Color {

        /**
         * Called after the fog color is calculated from the current block overlay or biome. Allows for modifying the
         * fog color.
         *
         * @param camera      the camera instance
         * @param partialTick the partial tick
         * @param fogRed      the red color component
         * @param fogGreen    the green color component
         * @param fogBlue     the blue color component
         */
        void onComputeFogColor(Camera camera, float partialTick, MutableFloat fogRed, MutableFloat fogGreen, MutableFloat fogBlue);
    }

    @FunctionalInterface
    public interface Setup {

        /**
         * Called before fog is rendered, allows for controlling fog start and end distance.
         *
         * @param camera         the camera instance
         * @param partialTick    the partial tick
         * @param fogEnvironment the fog environment implementation
         * @param fogType        the fog type
         * @param fogData        the fog data container
         */
        void onSetupFog(Camera camera, float partialTick, @Nullable FogEnvironment fogEnvironment, FogType fogType, FogData fogData);
    }
}
