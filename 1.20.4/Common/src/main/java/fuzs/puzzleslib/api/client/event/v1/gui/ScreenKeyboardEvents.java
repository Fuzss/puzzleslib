package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;

/**
 * Some javadoc has been copied from <code>net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents</code>.
 */
@SuppressWarnings("unchecked")
public final class ScreenKeyboardEvents {

    private ScreenKeyboardEvents() {

    }

    public static <T extends Screen> EventInvoker<BeforeKeyPress<T>> beforeKeyPress(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeKeyPress<T>>) (Class<?>) BeforeKeyPress.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterKeyPress<T>> afterKeyPress(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterKeyPress<T>>) (Class<?>) AfterKeyPress.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeKeyRelease<T>> beforeKeyRelease(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeKeyRelease<T>>) (Class<?>) BeforeKeyRelease.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterKeyRelease<T>> afterKeyRelease(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterKeyRelease<T>>) (Class<?>) AfterKeyRelease.class, screen);
    }

    @FunctionalInterface
    public interface BeforeKeyPress<T extends Screen> {

        /**
         * Called before a key press is handled.
         *
         * @param key       the named key code which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}
         * @param scanCode  the unique/platform-specific scan code of the keyboard input
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         * @return {@link EventResult#INTERRUPT} for marking the press event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeKeyPress(T screen, int key, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface AfterKeyPress<T extends Screen> {

        /**
         * Called after a key press is handled.
         *
         * @param key       the named key code which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}
         * @param scanCode  the unique/platform-specific scan code of the keyboard input
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         */
        void onAfterKeyPress(T screen, int key, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface BeforeKeyRelease<T extends Screen> {

        /**
         * Called before a pressed key has been released.
         *
         * @param key       the named key code which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}
         * @param scanCode  the unique/platform-specific scan code of the keyboard input
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         * @return {@link EventResult#INTERRUPT} for marking the release event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeKeyRelease(T screen, int key, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface AfterKeyRelease<T extends Screen> {

        /**
         * Called after a pressed key has been released.
         *
         * @param key       the named key code which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}
         * @param scanCode  the unique/platform-specific scan code of the keyboard input
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         */
        void onAfterKeyRelease(T screen, int key, int scanCode, int modifiers);
    }
}
