package fuzs.puzzleslib.api.client.key.v1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * An activation context for key mappings, allowing to restrict key mappings in regard to a screen being open or not.
 */
public enum KeyActivationContext {
    /**
     * A key mapping that is always processed no matter whether a screen is open or not.
     */
    UNIVERSAL(true, true),
    /**
     * A key mapping that is processed when the game is running without a screen being open in
     * {@link net.minecraft.client.Minecraft#screen}.
     * <p>
     * These keys are usually processed in {@link Minecraft#tick()} and corresponding events.
     */
    GAME(false, true),
    /**
     * A key mapping that is processed when a screen is open in {@link Minecraft#screen}
     * <p>
     * These keys are usually processed in {@link net.minecraft.client.gui.screens.Screen#keyPressed(KeyEvent)} and
     * {@link net.minecraft.client.gui.screens.Screen#mouseClicked(MouseButtonEvent, boolean)}.
     */
    SCREEN(true, false);

    private final boolean isScreenContext;
    private final boolean isGameContext;

    KeyActivationContext(boolean isScreenContext, boolean isGameContext) {
        this.isScreenContext = isScreenContext;
        this.isGameContext = isGameContext;
    }

    /**
     * @return is the activation context able to trigger the key binding
     */
    public boolean isSupportedEnvironment() {
        if (this.isScreenContext && Minecraft.getInstance().screen != null) {
            return true;
        } else if (this.isGameContext && Minecraft.getInstance().screen == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tests if this activation context is incompatible with another one.
     *
     * @param otherContext the other activation context
     * @return is the given activation context incompatible with this one
     */
    public boolean hasConflict(KeyActivationContext otherContext) {
        return this.isScreenContext != otherContext.isScreenContext && this.isGameContext != otherContext.isGameContext;
    }
}
