package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenKeyboardEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;

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
         * @param mouseButtonInfo the mouse button info; for bundled values see
         *                        {@link com.mojang.blaze3d.platform.InputConstants}
         * @param action          the mouse button action; see {@link InputConstants#RELEASE},
         *                        {@link InputConstants#PRESS}, {@link InputConstants#REPEAT}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onMouseClick(MouseButtonInfo mouseButtonInfo, int action);
    }

    @FunctionalInterface
    public interface MouseScroll {

        /**
         * Called before a mouse has scrolled without a screen being open.
         *
         * @param leftDown         is the left mouse button pressed
         * @param middleDown       is the middle mouse button pressed
         * @param rightDown        is the right mouse button pressed
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount   the vertical scroll amount
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
         * @param keyEvent the key event; for bundled values see {@link com.mojang.blaze3d.platform.InputConstants}
         * @param action   the mouse button action; see {@link InputConstants#RELEASE}, {@link InputConstants#PRESS},
         *                 {@link InputConstants#REPEAT}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as handled, it will not be passed to other listeners and vanilla behavior will not run</li>
         *         <li>{@link EventResult#PASS PASS} for letting other listeners as well as vanilla process this event</li>
         *         </ul>
         */
        EventResult onKeyPress(KeyEvent keyEvent, int action);
    }
}
