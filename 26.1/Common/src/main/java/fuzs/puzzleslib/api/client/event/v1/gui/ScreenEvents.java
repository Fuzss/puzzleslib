package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
public final class ScreenEvents {

    private ScreenEvents() {
        // NO-OP
    }

    public static <T extends Screen> EventInvoker<BeforeInit<T>> beforeInit(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeInit<T>>) (Class<?>) BeforeInit.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterInit<T>> afterInit(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterInit<T>>) (Class<?>) AfterInit.class, screen);
    }

    public static <T extends Screen> EventInvoker<Remove<T>> remove(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<Remove<T>>) (Class<?>) Remove.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeRender<T>> beforeRender(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeRender<T>>) (Class<?>) BeforeRender.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterBackground<T>> afterBackground(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterBackground<T>>) (Class<?>) AfterBackground.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterRender<T>> afterRender(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterRender<T>>) (Class<?>) AfterRender.class, screen);
    }

    @FunctionalInterface
    public interface BeforeInit<T extends Screen> {

        /**
         * Called before widgets on a {@link Screen} are cleared and rebuilt by calling {@link Screen#init()}, usually
         * triggered by the screen opening or being resized.
         * <p>
         * As opposed to Forge, this callback cannot be cancelled, and therefore also does not allow for manipulating
         * widgets, as those will be cleared anyway.
         * <p>
         * TODO remove Minecraft argument, the field on the screen is now final
         *
         * @param minecraft    the minecraft singleton instance
         * @param screen       the screen that is rebuilding widgets
         * @param screenWidth  width of the window
         * @param screenHeight height of the window
         * @param widgets      all old widgets on the screen provided as a read-only list, those will be cleared
         */
        void onBeforeInit(Minecraft minecraft, T screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets);
    }

    @FunctionalInterface
    public interface AfterInit<T extends Screen> {

        /**
         * Called after widgets on a {@link Screen} have been cleared and rebuilt by calling {@link Screen#init()},
         * usually triggered by the screen opening or being resized.
         * <p>
         * This callback allows for manipulating widgets on the screen, allowing for both additions and removals, as
         * well as modifications to existing widgets.
         * <p>
         * TODO remove Minecraft argument, the field on the screen is now final
         *
         * @param minecraft    the minecraft singleton instance
         * @param screen       the screen that is rebuilding widgets
         * @param screenWidth  width of the window
         * @param screenHeight height of the window
         * @param widgets      all widgets on the screen provided as a read-only list
         * @param addWidget    a consumer for adding a new widget
         * @param removeWidget a consumer for removing an existing widget
         */
        void onAfterInit(Minecraft minecraft, T screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, UnaryOperator<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget);
    }

    @FunctionalInterface
    public interface Remove<T extends Screen> {

        /**
         * Runs before a screen is removed in {@link Screen#removed()}.
         *
         * @param screen the currently displayed screen
         */
        void onRemove(T screen);
    }

    @FunctionalInterface
    public interface BeforeRender<T extends Screen> {

        /**
         * Runs before a screen is rendered in {@link Screen#render(GuiGraphics, int, int, float)}.
         *
         * @param screen      the currently displayed screen
         * @param guiGraphics the gui graphics component
         * @param mouseX      the x-position of the mouse
         * @param mouseY      the y-position of the mouse
         * @param partialTick the partial tick time
         */
        void onBeforeRender(T screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface AfterBackground<T extends Screen> {

        /**
         * Runs after a screen background is rendered in {@link Screen#renderBackground(GuiGraphics, int, int, float)}.
         *
         * @param screen      the currently displayed screen
         * @param guiGraphics the gui graphics component
         * @param mouseX      the x-position of the mouse
         * @param mouseY      the y-position of the mouse
         * @param partialTick the partial tick time
         */
        void onAfterBackground(T screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface AfterRender<T extends Screen> {

        /**
         * Runs after a screen is rendered in {@link Screen#render(GuiGraphics, int, int, float)}.
         *
         * @param screen      the currently displayed screen
         * @param guiGraphics the gui graphics component
         * @param mouseX      the x-position of the mouse
         * @param mouseY      the y-position of the mouse
         * @param partialTick the partial tick time
         */
        void onAfterRender(T screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
    }
}
