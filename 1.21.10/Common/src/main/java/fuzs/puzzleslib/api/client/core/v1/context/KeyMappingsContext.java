package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import net.minecraft.client.KeyMapping;

/**
 * Register a {@link KeyMapping} so it can be saved to and loaded from game options.
 */
public interface KeyMappingsContext {

    /**
     * Register a key mapping together for the universal activation context.
     *
     * @param keyMapping the key mapping
     */
    default void registerKeyMapping(KeyMapping keyMapping) {
        this.registerKeyMapping(keyMapping, KeyActivationContext.UNIVERSAL);
    }

    /**
     * Register a key mapping together with an activation context.
     *
     * @param keyMapping        the key mapping
     * @param activationContext an activation context for key mappings
     */
    default void registerKeyMapping(KeyMapping keyMapping, KeyActivationContext activationContext) {
        this.registerKeyMapping(keyMapping, KeyActivationHandler.direct(activationContext));
    }

    /**
     * Register a key mapping together with an activation handler.
     *
     * @param keyMapping        the key mapping
     * @param activationHandler an activation handler for key mappings
     */
    void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler);
}
