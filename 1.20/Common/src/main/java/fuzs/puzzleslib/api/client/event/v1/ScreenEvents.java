package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public final class ScreenEvents {
    @Deprecated(forRemoval = true)
    public static final EventInvoker<BeforeInit> BEFORE_INIT = EventInvoker.lookup(BeforeInit.class);
    @Deprecated(forRemoval = true)
    public static final EventInvoker<AfterInit> AFTER_INIT = EventInvoker.lookup(AfterInit.class);

    private ScreenEvents() {

    }

    public static <T extends Screen> EventInvoker<BeforeInitV2<T>> beforeInit(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeInitV2<T>>) (Class<?>) BeforeInitV2.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterInitV2<T>> afterInit(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterInitV2<T>>) (Class<?>) AfterInitV2.class, screen);
    }

    public static <T extends Screen> EventInvoker<Remove<T>> remove(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<Remove<T>>) (Class<?>) Remove.class, screen);
    }

    public static <T extends Screen> EventInvoker<BeforeRender<T>> beforeRender(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<BeforeRender<T>>) (Class<?>) BeforeRender.class, screen);
    }

    public static <T extends Screen> EventInvoker<AfterRender<T>> afterRender(Class<T> screen) {
        Objects.requireNonNull(screen, "screen type is null");
        return EventInvoker.lookup((Class<AfterRender<T>>) (Class<?>) AfterRender.class, screen);
    }

    @Deprecated(forRemoval = true)
    @FunctionalInterface
    public interface BeforeInit {

        /**
         * Called before widgets on a {@link Screen} are cleared and rebuilt by calling {@link Screen#init()},
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

    @Deprecated(forRemoval = true)
    @FunctionalInterface
    public interface AfterInit {

        /**
         * Called after widgets on a {@link Screen} have been cleared and rebuilt by calling {@link Screen#init()},
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

    @FunctionalInterface
    public interface BeforeInitV2<T extends Screen> {

        /**
         * Called before widgets on a {@link Screen} are cleared and rebuilt by calling {@link Screen#init()},
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
        void onBeforeInit(Minecraft minecraft, T screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets);
    }

    @FunctionalInterface
    public interface AfterInitV2<T extends Screen> {

        /**
         * Called after widgets on a {@link Screen} have been cleared and rebuilt by calling {@link Screen#init()},
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
        void onAfterInit(Minecraft minecraft, T screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, ConsumingOperator<AbstractWidget> addWidget, ConsumingOperator<AbstractWidget> removeWidget);
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
         * @param tickDelta   the partial tick time
         */
        void onBeforeRender(T screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta);
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
         * @param tickDelta   the partial tick time
         */
        void onAfterRender(T screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta);
    }

    /**
     * A simple helper class for returning the instance passed into a consumer.
     * <p>Similar to {@link net.minecraft.Util#make(Object, Consumer)}.
     *
     * @param <T> the handled type
     */
    public static final class ConsumingOperator<T> {
        private final Consumer<T> consumer;

        public ConsumingOperator(Consumer<T> consumer) {
            this.consumer = consumer;
        }

        /**
         * Apply the consumer and return the passed in instance.
         *
         * @param s   the instance passed to the consumer
         * @param <S> instance type
         * @return the passed in instance
         */
        public <S extends T> S apply(S s) {
            this.consumer.accept(s);
            return s;
        }
    }
}
