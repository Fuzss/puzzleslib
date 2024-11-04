package fuzs.puzzleslib.api.client.key.v1;

import net.minecraft.client.Minecraft;

/**
 * An activation context for key mappings, allowing to restrict key mappings in regard to a screen being open or not.
 */
public enum KeyActivationContext {
    /**
     * A key mapping that is always processed, no matter of whether a screen is open or not.
     */
    UNIVERSAL,
    /**
     * A key mapping that is processed when the game is running without a screen being open in
     * {@link net.minecraft.client.Minecraft#screen}.
     * <p>
     * These keys are usually processed in {@link Minecraft#tick()} and corresponding events.
     */
    GAME,
    /**
     * A key mapping that is processed when a screen is open in {@link Minecraft#screen}
     * <p>
     * These keys are usually processed in {@link net.minecraft.client.gui.screens.Screen#keyPressed(int, int, int)} and
     * {@link net.minecraft.client.gui.screens.Screen#mouseClicked(double, double, int)}.
     */
    SCREEN;

    /**
     * @return is the activation context able to trigger the key binding
     */
    public boolean isActive() {
        return switch (this) {
            case UNIVERSAL -> true;
            case SCREEN -> Minecraft.getInstance().screen != null;
            case GAME -> Minecraft.getInstance().screen == null;
        };
    }

    /**
     * Tests if this activation context is incompatible with another one.
     *
     * @param other the other activation context
     * @return is the given activation context incompatible with this one
     */
    public boolean isConflictingWith(KeyActivationContext other) {
        return this == UNIVERSAL || other == UNIVERSAL || this == other;
    }
}
