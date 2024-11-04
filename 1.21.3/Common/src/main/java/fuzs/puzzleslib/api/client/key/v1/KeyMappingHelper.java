package fuzs.puzzleslib.api.client.key.v1;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

/**
 * A small helper class for retrieving the registered {@link KeyActivationContext} for a {@link KeyMapping}.
 */
public interface KeyMappingHelper {
    KeyMappingHelper INSTANCE = ClientFactories.INSTANCE.getKeyMappingActivationHelper();

    /**
     * Retrieve the registered {@link KeyActivationContext} for a {@link KeyMapping}, will default to
     * {@link KeyActivationContext#UNIVERSAL}.
     *
     * @param keyMapping the key mapping
     * @return an activation context for key mappings
     */
    KeyActivationContext getKeyActivationContext(KeyMapping keyMapping);

    /**
     * Tests if two key mappings can coexist without interfering with each other.
     *
     * @param keyMapping      one key mapping
     * @param otherKeyMapping the other key mapping
     * @return can both key mappings coexist without interfering with each other
     */
    default boolean isConflictingWith(KeyMapping keyMapping, KeyMapping otherKeyMapping) {
        return this.getKeyActivationContext(keyMapping).isConflictingWith(this.getKeyActivationContext(otherKeyMapping));
    }

    /**
     * Register an unbound modded key mapping with a custom category.
     *
     * @param resourceLocation key mapping identifier for defining name and category keys
     * @return key mapping instance
     */
    static KeyMapping registerUnboundKeyMapping(ResourceLocation resourceLocation) {
        return registerKeyMapping(resourceLocation, InputConstants.UNKNOWN.getValue());
    }

    /**
     * Register a modded key mapping with a custom category.
     *
     * @param resourceLocation key mapping identifier for defining name and category keys
     * @param keyCode          the default key, get the value from {@link com.mojang.blaze3d.platform.InputConstants}
     * @return key mapping instance
     */
    static KeyMapping registerKeyMapping(ResourceLocation resourceLocation, int keyCode) {
        return new KeyMapping("key." + resourceLocation.getPath(),
                keyCode,
                "key.categories." + resourceLocation.getNamespace()
        );
    }
}
