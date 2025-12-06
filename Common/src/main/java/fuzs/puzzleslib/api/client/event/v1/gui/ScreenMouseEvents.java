package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.Objects;

@SuppressWarnings("unchecked")
public final class ScreenMouseEvents {

    private ScreenMouseEvents() {
        // NO-OP
    }

    public static <T extends Screen> EventInvoker<BeforeMouseClick<T>> beforeMouseClick(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseClick<T>>) (Class<?>) BeforeMouseClick.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseClick<T>> afterMouseClick(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseClick<T>>) (Class<?>) AfterMouseClick.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeMouseRelease<T>> beforeMouseRelease(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseRelease<T>>) (Class<?>) BeforeMouseRelease.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseRelease<T>> afterMouseRelease(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseRelease<T>>) (Class<?>) AfterMouseRelease.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeMouseScroll<T>> beforeMouseScroll(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseScroll<T>>) (Class<?>) BeforeMouseScroll.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseScroll<T>> afterMouseScroll(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseScroll<T>>) (Class<?>) AfterMouseScroll.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeMouseDrag<T>> beforeMouseDrag(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseDrag<T>>) (Class<?>) BeforeMouseDrag.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseDrag<T>> afterMouseDrag(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseDrag<T>>) (Class<?>) AfterMouseDrag.class, screen);
    }

    @FunctionalInterface
    public interface BeforeMouseClick<T extends Screen> {

        /**
         * Called before a mouse button is pressed on a screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseButtonEvent the mouse button event; for bundled values see
         *                         {@link com.mojang.blaze3d.platform.InputConstants}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the click event as handled, it will not be passed to other listeners and vanilla behaviour will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onBeforeMouseClick(T screen, MouseButtonEvent mouseButtonEvent);
    }

    @FunctionalInterface
    public interface AfterMouseClick<T extends Screen> {

        /**
         * Called after a mouse button is pressed on a screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseButtonEvent the mouse button event; for bundled values see
         *                         {@link com.mojang.blaze3d.platform.InputConstants}
         */
        void onAfterMouseClick(T screen, MouseButtonEvent mouseButtonEvent);
    }

    @FunctionalInterface
    public interface BeforeMouseRelease<T extends Screen> {

        /**
         * Called before a mouse click has released on a screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseButtonEvent the mouse button event; for bundled values see
         *                         {@link com.mojang.blaze3d.platform.InputConstants}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the release event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onBeforeMouseRelease(T screen, MouseButtonEvent mouseButtonEvent);
    }

    @FunctionalInterface
    public interface AfterMouseRelease<T extends Screen> {

        /**
         * Called after a mouse click has released on a screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseButtonEvent the mouse button event; for bundled values see
         *                         {@link com.mojang.blaze3d.platform.InputConstants}
         */
        void onAfterMouseRelease(T screen, MouseButtonEvent mouseButtonEvent);
    }

    @FunctionalInterface
    public interface BeforeMouseScroll<T extends Screen> {

        /**
         * Called before a mouse has scrolled on a screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseX           the x-position of the mouse cursor
         * @param mouseY           the y-position of the mouse cursor
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount   the vertical scroll amount
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the scroll event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onBeforeMouseScroll(T screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    }

    @FunctionalInterface
    public interface AfterMouseScroll<T extends Screen> {

        /**
         * Called after a mouse has scrolled on a screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseX           the x-position of the mouse cursor
         * @param mouseY           the y-position of the mouse cursor
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount   the vertical scroll amount
         */
        void onAfterMouseScroll(T screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    }

    @FunctionalInterface
    public interface BeforeMouseDrag<T extends Screen> {

        /**
         * Called before a mouse is dragged on screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseButtonEvent the mouse button event; for bundled values see
         *                         {@link com.mojang.blaze3d.platform.InputConstants}
         * @param dragX            the horizontal amount the cursor has been dragged since the last drag event
         * @param dragY            the vertical amount the cursor has been dragged since the last drag event
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the drag event as handled, it will not be passed to other listeners and vanilla behaviour will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onBeforeMouseDrag(T screen, MouseButtonEvent mouseButtonEvent, double dragX, double dragY);
    }

    @FunctionalInterface
    public interface AfterMouseDrag<T extends Screen> {

        /**
         * Called after a mouse is dragged on screen.
         *
         * @param screen           the currently displayed screen
         * @param mouseButtonEvent the mouse button event; for bundled values see
         *                         {@link com.mojang.blaze3d.platform.InputConstants}
         * @param dragX            the horizontal amount the cursor has been dragged since the last drag event
         * @param dragY            the vertical amount the cursor has been dragged since the last drag event
         */
        void onAfterMouseDrag(T screen, MouseButtonEvent mouseButtonEvent, double dragX, double dragY);
    }
}
