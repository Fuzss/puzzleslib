package fuzs.puzzleslib.api.client.core.v1.contexts;

import net.minecraft.client.KeyMapping;

/**
 * register a {@link KeyMapping} so it can be saved to and loaded from game options
 */
@FunctionalInterface
public interface KeyMappingsContext {

    /**
     * Forge supports much more here for the key mapping (like conflicts, and modifiers, but we keep it simple for the sake of Fabric)
     *
     * @param keyMappings the key mappings to register
     */
    void registerKeyMappings(KeyMapping... keyMappings);
}
