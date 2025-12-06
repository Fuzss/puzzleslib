package fuzs.puzzleslib.api.client.key.v1;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

/**
 * A small helper class for retrieving the registered {@link KeyActivationContext} for a {@link KeyMapping}.
 */
public interface KeyMappingHelper {
    KeyMappingHelper INSTANCE = ClientProxyImpl.get().getKeyMappingActivationHelper();
    /**
     * @see net.minecraft.resources.ResourceKey#VALUES
     */
    @ApiStatus.Internal
    Map<ResourceLocation, KeyMapping.Category> KEY_CATEGORIES = new MapMaker().weakValues().makeMap();

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
        return this.getKeyActivationContext(keyMapping).hasConflict(this.getKeyActivationContext(otherKeyMapping));
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
        return new KeyMapping("key." + resourceLocation.toLanguageKey(),
                keyCode,
                KEY_CATEGORIES.computeIfAbsent(resourceLocation.withPath("main"), KeyMapping.Category::new));
    }

    /**
     * Checks if a key mapping is pressed.
     * <p>
     * NeoForge replaces the vanilla call to {@link KeyMapping#matches(KeyEvent)} in a few places to account for key
     * activation contexts (game &amp; screen environments).
     *
     * @param keyMapping the key mapping
     * @param keyEvent   the key event
     * @return is the key mapping pressed
     */
    static boolean isKeyActiveAndMatches(KeyMapping keyMapping, KeyEvent keyEvent) {
        return ClientProxyImpl.get().isKeyActiveAndMatches(keyMapping, keyEvent);
    }

    /**
     * Checks if a key mapping is pressed.
     * <p>
     * Similar to {@link KeyMapping#matches(KeyEvent)}, but for code points.
     * <p>
     * Useful when working with {@link net.minecraft.client.KeyboardHandler#charTyped(long, CharacterEvent)}.
     *
     * @param keyMapping the key mapping
     * @param codePoint  the code point
     * @return is the key mapping pressed
     */
    static boolean matchesCodePoint(KeyMapping keyMapping, int codePoint) {
        if (keyMapping.key.getType() == InputConstants.Type.KEYSYM && !keyMapping.isUnbound()) {
            String string = new String(Character.toChars(codePoint));
            String keyName = GLFW.glfwGetKeyName(keyMapping.key.getValue(), -1);
            return keyName != null && keyName.equalsIgnoreCase(string);
        } else {
            return false;
        }
    }
}
