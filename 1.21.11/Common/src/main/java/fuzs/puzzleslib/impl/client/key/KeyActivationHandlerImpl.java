package fuzs.puzzleslib.impl.client.key;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public record KeyActivationHandlerImpl(@Nullable Consumer<Minecraft> gameHandler,
                                       @Nullable Class<? extends Screen> screenType,
                                       @Nullable Consumer<? extends Screen> screenHandler) implements KeyActivationHandler {

    @Override
    public KeyActivationHandler withGameHandler(Consumer<Minecraft> gameHandler) {
        Objects.requireNonNull(gameHandler, "game handler is null");
        return new KeyActivationHandlerImpl(gameHandler, this.screenType, this.screenHandler);
    }

    @Override
    public <T extends Screen> KeyActivationHandler withScreenHandler(Class<T> screenType, Consumer<T> screenHandler) {
        Objects.requireNonNull(screenType, "screen type is null");
        Objects.requireNonNull(screenHandler, "screen handler is null");
        return new KeyActivationHandlerImpl(this.gameHandler, screenType, screenHandler);
    }

    @Override
    public KeyActivationContext getActivationContext() {
        if (this.gameHandler != null && this.screenHandler != null) {
            return KeyActivationContext.UNIVERSAL;
        } else if (this.gameHandler != null) {
            return KeyActivationContext.GAME;
        } else if (this.screenHandler != null) {
            return KeyActivationContext.SCREEN;
        } else {
            throw new IllegalStateException("Key activation handler has no handlers!");
        }
    }
}
