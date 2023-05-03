package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;

/**
 * Very similar to {@link ScreenMouseEvents} and {@link ScreenKeyboardEvents}, but fires when no screen is open to handle input events in the {@link net.minecraft.client.gui.Gui}.
 */
public final class InputEvents {
    public static final EventInvoker<BeforeMouseClick> BEFORE_MOUSE_CLICK = EventInvoker.lookup(BeforeMouseClick.class);
    public static final EventInvoker<AfterMouseClick> AFTER_MOUSE_CLICK = EventInvoker.lookup(AfterMouseClick.class);
    public static final EventInvoker<BeforeMouseRelease> BEFORE_MOUSE_RELEASE = EventInvoker.lookup(BeforeMouseRelease.class);
    public static final EventInvoker<AfterMouseRelease> AFTER_MOUSE_RELEASE = EventInvoker.lookup(AfterMouseRelease.class);
    public static final EventInvoker<BeforeMouseScroll> BEFORE_MOUSE_SCROLL = EventInvoker.lookup(BeforeMouseScroll.class);
    public static final EventInvoker<AfterMouseScroll> AFTER_MOUSE_SCROLL = EventInvoker.lookup(AfterMouseScroll.class);

    private InputEvents() {

    }

    @FunctionalInterface
    public interface BeforeMouseClick {

        /**
         * Called before a mouse button is pressed without a screen being open.
         *
         * @param button    the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @param modifiers a bit field representing the active modifier keys
         * @return {@link EventResult#INTERRUPT} for marking the click event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseClick(int button, int modifiers);
    }

    @FunctionalInterface
    public interface AfterMouseClick {

        /**
         * Called after a mouse button is pressed without a screen being open.
         *
         * @param button    the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @param modifiers a bit field representing the active modifier keys
         */
        void onAfterMouseClick(int button, int modifiers);
    }

    @FunctionalInterface
    public interface BeforeMouseRelease {

        /**
         * Called before a mouse click has released without a screen being open.
         *
         * @param button    the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @param modifiers a bit field representing the active modifier keys
         * @return {@link EventResult#INTERRUPT} for marking the release event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseRelease(int button, int modifiers);
    }

    @FunctionalInterface
    public interface AfterMouseRelease {

        /**
         * Called after a mouse click has released without a screen being open.
         *
         * @param button    the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @param modifiers a bit field representing the active modifier keys
         */
        void onAfterMouseRelease(int button, int modifiers);
    }

    @FunctionalInterface
    public interface BeforeMouseScroll {

        /**
         * Called before a mouse has scrolled without a screen being open.
         *
         * @param leftDown         is the left mouse button pressed
         * @param middleDown       is the middle mouse button pressed
         * @param rightDown        is the right mouse button pressed
         * @param horizontalAmount horizontal scroll amount
         * @param verticalAmount   vertical scroll amount
         * @return {@link EventResult#INTERRUPT} for marking the scroll event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseScroll(boolean leftDown, boolean middleDown, boolean rightDown, double horizontalAmount, double verticalAmount);
    }

    @FunctionalInterface
    public interface AfterMouseScroll {

        /**
         * Called after a mouse has scrolled without a screen being open.
         *
         * @param leftDown         is the left mouse button pressed
         * @param middleDown       is the middle mouse button pressed
         * @param rightDown        is the right mouse button pressed
         * @param horizontalAmount horizontal scroll amount
         * @param verticalAmount   vertical scroll amount
         */
        void onAfterMouseScroll(boolean leftDown, boolean middleDown, boolean rightDown, double horizontalAmount, double verticalAmount);
    }
}
