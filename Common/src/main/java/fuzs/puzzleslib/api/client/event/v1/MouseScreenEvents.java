package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;

@SuppressWarnings("unchecked")
public final class MouseScreenEvents {

    private MouseScreenEvents() {

    }

    public static <T extends Screen> EventInvoker<BeforeMouseScroll<T>> beforeMouseScroll(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseScroll<T>>) (Class<?>) BeforeMouseScroll.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseScroll<T>> afterMouseScroll(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseScroll<T>>) (Class<?>) BeforeMouseScroll.class, screen);
    }

    @FunctionalInterface
    public interface BeforeMouseScroll<T extends Screen> {

        /**
         * Called before a mouse has scrolled on a screen.
         *
         * @param screen           the screen the mouse is scrolling on
         * @param mouseX           x position of the mouse cursor
         * @param mouseY           y position of the mouse cursor
         * @param horizontalAmount horizontal scroll amount
         * @param verticalAmount   vertical scroll amount
         * @return {@link EventResult#INTERRUPT} for marking the scroll event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseScroll(T screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    }

    @FunctionalInterface
    public interface AfterMouseScroll<T extends Screen> {

        /**
         * Called after a mouse has scrolled on a screen.
         *
         * @param mouseX           x position of the mouse cursor
         * @param mouseY           y position of the mouse cursor
         * @param horizontalAmount horizontal scroll amount
         * @param verticalAmount   vertical scroll amount
         */
        void onAfterMouseScroll(T screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    }
}
