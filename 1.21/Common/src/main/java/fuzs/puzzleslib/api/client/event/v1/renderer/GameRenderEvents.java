package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public final class GameRenderEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private GameRenderEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Fires before the game and level are rendered in {@link GameRenderer#render(float, long, boolean)}.
         *
         * @param minecraft    minecraft singleton instance
         * @param gameRenderer game renderer instance
         * @param deltaTracker  partial tick time, different when the game is paused
         */
        void onBeforeGameRender(Minecraft minecraft, GameRenderer gameRenderer, DeltaTracker deltaTracker);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Fires after the game and level are rendered in {@link GameRenderer#render(float, long, boolean)}.
         *
         * @param minecraft    minecraft singleton instance
         * @param gameRenderer game renderer instance
         * @param deltaTracker  partial tick time, different when the game is paused
         */
        void onAfterGameRender(Minecraft minecraft, GameRenderer gameRenderer, DeltaTracker deltaTracker);
    }
}
