package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import net.minecraft.client.KeyMapping;

import java.util.Objects;

/**
 * Register a {@link KeyMapping} so it can be saved to and loaded from game options.
 */
@FunctionalInterface
public interface KeyMappingsContext {

    /**
     * Forge supports much more here for the key mapping (like conflicts, and modifiers, but we keep it simple for the sake of Fabric).
     *
     * @param keyMappings the key mappings to register
     *
     * @deprecated migrate to {@link #registerKeyMapping(KeyMapping, KeyMappingActivationHelper.KeyActivationContext)}
     */
    @Deprecated(forRemoval = true)
    default void registerKeyMapping(KeyMapping... keyMappings) {
        Objects.requireNonNull(keyMappings, "key mappings is null");
        Preconditions.checkPositionIndex(1, keyMappings.length, "key mappings is empty");
        for (KeyMapping keyMapping : keyMappings) {
            Objects.requireNonNull(keyMapping, "key mapping is null");
            this.registerKeyMapping(keyMapping, KeyMappingActivationHelper.KeyActivationContext.UNIVERSAL);
        }
    }

    /**
     * Register a key mapping together with an activation context.
     *
     * @param keyMapping the key mapping
     * @param keyActivationContext an activation context for key mappings
     */
    void registerKeyMapping(KeyMapping keyMapping, KeyMappingActivationHelper.KeyActivationContext keyActivationContext);
}
