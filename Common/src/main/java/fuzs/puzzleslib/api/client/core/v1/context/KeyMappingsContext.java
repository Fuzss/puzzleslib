package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import net.minecraft.client.KeyMapping;

import java.util.Objects;

/**
 * Register a {@link KeyMapping} so it can be saved to and loaded from game options.
 */
@FunctionalInterface
public interface KeyMappingsContext {

    /**
     * Register a key mapping together for the universal activation context.
     *
     * @param keyMapping the key mapping
     */
    default void registerKeyMapping(KeyMapping keyMapping) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        this.registerKeyMapping(keyMapping, KeyActivationContext.UNIVERSAL);
    }

    /**
     * Register a key mapping together with an activation context.
     *
     * @param keyMapping the key mapping
     * @param keyActivationContext an activation context for key mappings
     */
    void registerKeyMapping(KeyMapping keyMapping, KeyActivationContext keyActivationContext);
}
