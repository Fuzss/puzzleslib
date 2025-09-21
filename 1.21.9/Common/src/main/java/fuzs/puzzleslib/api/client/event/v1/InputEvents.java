package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenKeyboardEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;

/**
 * Very similar to {@link ScreenMouseEvents} and {@link ScreenKeyboardEvents}, but fires when no screen is open to
 * handle input events in the {@link net.minecraft.client.gui.Gui}.
 */
public final class InputEvents {
    public static final EventInvoker<MouseClick> MOUSE_CLICK = EventInvoker.lookup(MouseClick.class);
    public static final EventInvoker<MouseScroll> MOUSE_SCROLL = EventInvoker.lookup(MouseScroll.class);
    public static final EventInvoker<KeyPress> KEY_PRESS = EventInvoker.lookup(KeyPress.class);

    private InputEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface MouseClick {

        /**
         * Called before a mouse button is clicked or released without a screen being open.
         *
         * @param mouseButton the button input code, see {@link InputConstants}
         * @param action      the mouse button action, see {@link InputConstants}
         * @param modifiers   a bit field representing the active modifier keys
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onMouseClick(int mouseButton, int action, int modifiers);
    }

    @FunctionalInterface
    public interface MouseScroll {

        /**
         * Called before a mouse has scrolled without a screen being open.
         *
         * @param leftDown         is the left mouse button pressed
         * @param middleDown       is the middle mouse button pressed
         * @param rightDown        is the right mouse button pressed
         * @param horizontalAmount horizontal scroll amount
         * @param verticalAmount   vertical scroll amount
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onMouseScroll(boolean leftDown, boolean middleDown, boolean rightDown, double horizontalAmount, double verticalAmount);
    }

    @FunctionalInterface
    public interface KeyPress {

        /**
         * Called before a key press, release or repeat action is handled.
         * <p>
         * Note that on NeoForge due to the native implementation of this event cancelling a key press is not
         * supported.
         *
         * @param keyCode   the named key code which can be identified by the constants in {@link InputConstants}
         * @param scanCode  the unique / platform-specific scan code of the keyboard input
         * @param action    the key action, see {@link InputConstants}
         * @param modifiers a GLFW bitfield describing the modifier keys that are held down
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onKeyPress(int keyCode, int scanCode, int action, int modifiers);
    }
}
