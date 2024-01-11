package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;

/**
 * Very similar to {@link ScreenMouseEvents} and {@link ScreenKeyboardEvents}, but fires when no screen is open to handle input events in the {@link net.minecraft.client.gui.Gui}.
 * Some events even fire before a screen has had a chance to handle the input, depends on the exact implementation which follows Forge.
 */
public final class InputEvents {
    public static final EventInvoker<BeforeMouseAction> BEFORE_MOUSE_ACTION = EventInvoker.lookup(BeforeMouseAction.class);
    public static final EventInvoker<AfterMouseAction> AFTER_MOUSE_ACTION = EventInvoker.lookup(AfterMouseAction.class);
    public static final EventInvoker<BeforeMouseScroll> BEFORE_MOUSE_SCROLL = EventInvoker.lookup(BeforeMouseScroll.class);
    public static final EventInvoker<AfterMouseScroll> AFTER_MOUSE_SCROLL = EventInvoker.lookup(AfterMouseScroll.class);
    public static final EventInvoker<BeforeKeyAction> BEFORE_KEY_ACTION = EventInvoker.lookup(BeforeKeyAction.class);
    public static final EventInvoker<AfterKeyAction> AFTER_KEY_ACTION = EventInvoker.lookup(AfterKeyAction.class);

    private InputEvents() {

    }

    @FunctionalInterface
    public interface BeforeMouseAction {

        /**
         * Called before a mouse button is clicked or released without a screen being open.
         *
         * @param button    the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @param action    the mouse button action, see {@link InputConstants}
         * @param modifiers a bit field representing the active modifier keys
         * @return {@link EventResult#INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeMouseAction(int button, int action, int modifiers);
    }

    @FunctionalInterface
    public interface AfterMouseAction {

        /**
         * Called after a mouse button is clicked or released without a screen being open.
         *
         * @param button    the button input code, see {@link org.lwjgl.glfw.GLFW}
         * @param action    the mouse button action, see {@link InputConstants}
         * @param modifiers a bit field representing the active modifier keys
         */
        void onAfterMouseAction(int button, int action, int modifiers);
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
         * @return {@link EventResult#INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run,
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

    @FunctionalInterface
    public interface BeforeKeyAction {

        /**
         * Called before a key press, release or repeat action is handled.
         *
         * @param key       the named key code which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}
         * @param scanCode  the unique/platform-specific scan code of the keyboard input
         * @param action    the key action, see {@link InputConstants}
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         * @return {@link EventResult#INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run,
         * {@link EventResult#PASS} for letting other listeners as well as vanilla process this event
         */
        EventResult onBeforeKeyAction(int key, int scanCode, int action, int modifiers);
    }

    @FunctionalInterface
    public interface AfterKeyAction {

        /**
         * Called after a key press, release or repeat action is handled.
         *
         * @param key       the named key code which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}
         * @param scanCode  the unique/platform-specific scan code of the keyboard input
         * @param action    the key action, see {@link InputConstants}
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         */
        void onAfterKeyAction(int key, int scanCode, int action, int modifiers);
    }
}
