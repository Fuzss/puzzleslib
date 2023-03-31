package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.function.Consumer;

public final class ScreenEvents {
    public static final EventInvoker<BeforeInit> BEFORE_INIT = EventInvoker.lookup(BeforeInit.class);
    public static final EventInvoker<AfterInit> AFTER_INIT = EventInvoker.lookup(AfterInit.class);

    private ScreenEvents() {

    }

    @FunctionalInterface
    public interface BeforeInit {

        /**
         * Called before widgets on a {@link Screen} are cleared and rebuilt by calling the <code>init</code> method,
         * usually triggered by the screen opening or being resized.
         * <p>As opposed to Forge, this callback cannot be cancelled, and therefore also does not allow for manipulating widgets,
         * as those will be cleared anyway.
         *
         * @param minecraft    the minecraft singleton instance
         * @param screen       the screen that is rebuilding widgets
         * @param screenWidth  width of the window
         * @param screenHeight height of the window
         * @param widgets      all old widgets on the screen provided as a read-only list, those will be cleared
         */
        void onBeforeInit(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets);
    }

    @FunctionalInterface
    public interface AfterInit {

        /**
         * Called after widgets on a {@link Screen} have been cleared and rebuilt by calling the <code>init</code> method,
         * usually triggered by the screen opening or being resized.
         * <p>This callback allows for manipulating widgets on the screen, allowing for both additions and removals,
         * as well as modifications to existing widgets.
         *
         * @param minecraft    the minecraft singleton instance
         * @param screen       the screen that is rebuilding widgets
         * @param screenWidth  width of the window
         * @param screenHeight height of the window
         * @param widgets      all widgets on the screen provided as a read-only list
         * @param addWidget    a consumer for adding a new widget
         * @param removeWidget a consumer for removing an existing widget, obtain the widget instance from <code>widgets</code>
         */
        void onAfterInit(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget);
    }
}
