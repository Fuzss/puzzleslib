package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;

/**
 * Some javadoc has been copied from <code>net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents</code>.
 */
@SuppressWarnings("unchecked")
public final class ScreenMouseEvents {

    private ScreenMouseEvents() {

    }

    public static <T extends Screen> EventInvoker<BeforeMouseClick<T>> beforeMouseClick(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseClick<T>>) (Class<?>) BeforeMouseClick.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseClick<T>> afterMouseClick(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseClick<T>>) (Class<?>) BeforeMouseClick.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeMouseRelease<T>> beforeMouseRelease(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseRelease<T>>) (Class<?>) BeforeMouseRelease.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseRelease<T>> afterMouseRelease(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseRelease<T>>) (Class<?>) BeforeMouseRelease.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeMouseScroll<T>> beforeMouseScroll(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseScroll<T>>) (Class<?>) BeforeMouseScroll.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseScroll<T>> afterMouseScroll(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseScroll<T>>) (Class<?>) BeforeMouseScroll.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeMouseDrag<T>> beforeMouseDrag(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeMouseDrag<T>>) (Class<?>) BeforeMouseDrag.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterMouseDrag<T>> afterMouseDrag(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterMouseDrag<T>>) (Class<?>) BeforeMouseDrag.class, screen);
    }

    @FunctionalInterface
    public interface BeforeMouseClick<T extends Screen> {

        /**
         * Called before a mouse button is pressed in a screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX the x-position of the mouse
         * @param mouseY the y-position of the mouse
         * @param button the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @return {@link EventResult#INTERRUPT} for marking the click event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseClick(T screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface AfterMouseClick<T extends Screen> {

        /**
         * Called after a mouse button is pressed in a screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX the x-position of the mouse
         * @param mouseY the y-position of the mouse
         * @param button the button input code, see {@link org.lwjgl.glfw.GLFW}
         */
        void onAfterMouseClick(T screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface BeforeMouseRelease<T extends Screen> {

        /**
         * Called before a mouse click has released in a screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX the x-position of the mouse
         * @param mouseY the y-position of the mouse
         * @param button the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @return {@link EventResult#INTERRUPT} for marking the release event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseRelease(T screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface AfterMouseRelease<T extends Screen> {

        /**
         * Called after a mouse click has released in a screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX the x-position of the mouse
         * @param mouseY the y-position of the mouse
         * @param button the button input code, see {@link org.lwjgl.glfw.GLFW}
         */
        void onAfterMouseRelease(T screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface BeforeMouseScroll<T extends Screen> {

        /**
         * Called before a mouse has scrolled on a screen.
         *
         * @param screen           the screen the mouse is scrolling on
         * @param mouseX           x-position of the mouse cursor
         * @param mouseY           y-position of the mouse cursor
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
         * @param screen           the screen the mouse is scrolling on
         * @param mouseX           x-position of the mouse cursor
         * @param mouseY           y-position of the mouse cursor
         * @param horizontalAmount horizontal scroll amount
         * @param verticalAmount   vertical scroll amount
         */
        void onAfterMouseScroll(T screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
    }

    @FunctionalInterface
    public interface BeforeMouseDrag<T extends Screen> {

        /**
         * Called before a mouse is dragged on screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX mouse x-position
         * @param mouseY mouse y-position
         * @param button mouse button that was clicked
         * @param dragX  how far the cursor has been dragged since last calling this on x
         * @param dragY  how far the cursor has been dragged since last calling this on y
         * @return {@link EventResult#INTERRUPT} for marking the drag event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseDrag(T screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }

    @FunctionalInterface
    public interface AfterMouseDrag<T extends Screen> {

        /**
         * Called after a mouse is dragged on screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX mouse x-position
         * @param mouseY mouse y-position
         * @param button mouse button that was clicked
         * @param dragX  how far the cursor has been dragged since last calling this on x
         * @param dragY  how far the cursor has been dragged since last calling this on y
         */
        void onAfterMouseDrag(T screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }
}
