package fuzs.puzzleslib.api.client.screen.v2;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/**
 * A small helper class for retrieving the registered {@link KeyActivationContext} for a {@link KeyMapping}.
 */
public interface KeyMappingActivationHelper {
    KeyMappingActivationHelper INSTANCE = ClientFactories.INSTANCE.getKeyMappingActivationHelper();

    /**
     * Retrieve the registered {@link KeyActivationContext} for a {@link KeyMapping}, will default to {@link KeyActivationContext#UNIVERSAL}.
     *
     * @param keyMapping the key mapping
     * @return an activation context for key mappings
     */
    KeyActivationContext getKeyActivationContext(KeyMapping keyMapping);

    /**
     * Tests if two key mappings can coexist without interfering with each other.
     *
     * @param keyMapping one key mapping
     * @param other the other key mapping
     * @return can both key mappings coexist without interfering with each other
     */
    default boolean hasConflictWith(KeyMapping keyMapping, KeyMapping other) {
        return this.getKeyActivationContext(keyMapping).hasConflictWith(this.getKeyActivationContext(other));
    }

    /**
     * An activation context for key mappings, allowing to restrict key mappings in regard to a screen being open or not.
     */
    enum KeyActivationContext {
        /**
         * A key mapping that is always processed, no matter of whether a screen is open or not.
         */
        UNIVERSAL,
        /**
         * A key mapping that is processed when the game is running without a screen being open in {@link Minecraft#screen}.
         * <p>These keys are usually processed in {@link Minecraft#tick()} and corresponding events.
         */
        GAME,
        /**
         * A key mapping that is processed when a screen is open in {@link Minecraft#screen}
         * <p>These keys are usually processed in {@link net.minecraft.client.gui.screens.Screen#keyPressed(int, int, int)} and {@link net.minecraft.client.gui.screens.Screen#mouseClicked(double, double, int)}.
         */
        SCREEN;

        /**
         * Tests if this activation context is incompatible with another one.
         *
         * @param other the other activation context
         * @return is the given activation context incompatible with this one
         */
        public boolean hasConflictWith(KeyActivationContext other) {
            return this == UNIVERSAL || other == UNIVERSAL || this == other;
        }
    }
}
