package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;

import java.util.Objects;

@SuppressWarnings("unchecked")
public final class ScreenKeyboardEvents {

    private ScreenKeyboardEvents() {
        // NO-OP
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
         * @param screen   the currently displayed screen
         * @param keyEvent the key event; for bundled values see {@link com.mojang.blaze3d.platform.InputConstants}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the press event as handled, it will not be passed to other listeners and vanilla behaviour will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onBeforeKeyPress(T screen, KeyEvent keyEvent);
    }

    @FunctionalInterface
    public interface AfterKeyPress<T extends Screen> {

        /**
         * Called after a key press is handled.
         *
         * @param screen   the currently displayed screen
         * @param keyEvent the key event; for bundled values see {@link com.mojang.blaze3d.platform.InputConstants}
         */
        void onAfterKeyPress(T screen, KeyEvent keyEvent);
    }

    @FunctionalInterface
    public interface BeforeKeyRelease<T extends Screen> {

        /**
         * Called before a pressed key has been released.
         *
         * @param screen   the currently displayed screen
         * @param keyEvent the key event; for bundled values see {@link com.mojang.blaze3d.platform.InputConstants}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the release event as handled, it will not be passed to other listeners and vanilla behaviour will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onBeforeKeyRelease(T screen, KeyEvent keyEvent);
    }

    @FunctionalInterface
    public interface AfterKeyRelease<T extends Screen> {

        /**
         * Called after a pressed key has been released.
         *
         * @param screen   the currently displayed screen
         * @param keyEvent the key event; for bundled values see {@link com.mojang.blaze3d.platform.InputConstants}
         */
        void onAfterKeyRelease(T screen, KeyEvent keyEvent);
    }
}
