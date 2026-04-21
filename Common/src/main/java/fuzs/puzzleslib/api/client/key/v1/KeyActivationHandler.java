package fuzs.puzzleslib.api.client.key.v1;

import fuzs.puzzleslib.impl.client.key.KeyActivationHandlerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A storage class for handling activated key mappings for different types of {@link KeyActivationContext}.
 */
@FunctionalInterface
public interface KeyActivationHandler {

    /**
     * A basic activation handler from an activation context that does not register any events.
     *
     * @param activationContext activation context
     * @return activation handler
     */
    static KeyActivationHandler direct(KeyActivationContext activationContext) {
        return () -> activationContext;
    }

    /**
     * An activation handler in builder-like format.
     *
     * @return activation handler
     */
    static KeyActivationHandler of() {
        return new KeyActivationHandlerImpl(null, null, null);
    }

    /**
     * An activation handler for {@link KeyActivationContext#GAME}.
     *
     * @param gameHandler handler for processing the key press at the beginning of each client tick
     * @return activation handler
     */
    static KeyActivationHandler forGame(Consumer<Minecraft> gameHandler) {
        Objects.requireNonNull(gameHandler, "game handler is null");
        return new KeyActivationHandlerImpl(gameHandler, null, null);
    }

    /**
     * An activation handler for {@link KeyActivationContext#SCREEN}.
     *
     * @param screenHandler handler for processing the key press in {@link Screen#keyPressed(KeyEvent)}
     * @return activation handler
     */
    static KeyActivationHandler forScreen(Consumer<Screen> screenHandler) {
        return forScreen(Screen.class, screenHandler);
    }

    /**
     * An activation handler for {@link KeyActivationContext#SCREEN}.
     *
     * @param screenType    screen super type to register the screen handler for
     * @param screenHandler handler for processing the key press in {@link Screen#keyPressed(KeyEvent)}
     * @param <T>           screen super type
     * @return activation handler
     */
    static <T extends Screen> KeyActivationHandler forScreen(Class<T> screenType, Consumer<T> screenHandler) {
        Objects.requireNonNull(screenType, "screen type is null");
        Objects.requireNonNull(screenHandler, "screen handler is null");
        return new KeyActivationHandlerImpl(null, screenType, screenHandler);
    }

    /**
     * @return activation context based on what handlers are present
     */
    KeyActivationContext getActivationContext();

    /**
     * @return handler for processing the key press at the beginning of each client tick
     */
    @Nullable default Consumer<Minecraft> gameHandler() {
        return null;
    }

    /**
     * @return screen super type to register the screen handler for
     */
    @Nullable default Class<? extends Screen> screenType() {
        return null;
    }

    /**
     * @return handler for processing the key press in {@link Screen#keyPressed(KeyEvent)}
     */
    @Nullable default Consumer<? extends Screen> screenHandler() {
        return null;
    }

    /**
     * @param gameHandler handler for processing the key press at the beginning of each client tick
     * @return builder instance
     */
    default KeyActivationHandler withGameHandler(Consumer<Minecraft> gameHandler) {
        return this;
    }

    /**
     * @param screenHandler handler for processing the key press in {@link Screen#keyPressed(KeyEvent)}
     * @return builder instance
     */
    default KeyActivationHandler withScreenHandler(Consumer<Screen> screenHandler) {
        return this.withScreenHandler(Screen.class, screenHandler);
    }

    /**
     * @param screenType    screen super type to register the screen handler for
     * @param screenHandler handler for processing the key press in {@link Screen#keyPressed(KeyEvent)}
     * @param <T>           screen super type
     * @return builder instance
     */
    default <T extends Screen> KeyActivationHandler withScreenHandler(Class<T> screenType, Consumer<T> screenHandler) {
        return this;
    }
}
