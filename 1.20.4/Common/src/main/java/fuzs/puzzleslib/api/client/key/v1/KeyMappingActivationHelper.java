package fuzs.puzzleslib.api.client.key.v1;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.KeyMapping;

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
}
