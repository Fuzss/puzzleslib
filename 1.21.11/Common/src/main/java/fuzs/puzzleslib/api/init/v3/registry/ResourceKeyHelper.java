package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

/**
 * A utility class for creating {@link Component Components} from {@link ResourceKey ResourceKeys}.
 */
public final class ResourceKeyHelper {

    private ResourceKeyHelper() {
        // NO-OP
    }

    /**
     * Useful for name components for various data pack registry entries.
     *
     * @param resourceKey the resource key
     * @return the component
     */
    public static MutableComponent getComponent(ResourceKey<?> resourceKey) {
        return Component.translatable(getTranslationKey(resourceKey));
    }

    /**
     * Useful for name components for various data pack registry entries.
     *
     * @param registryKey      the registry key
     * @param identifier the identifier
     * @return the component
     */
    public static MutableComponent getComponent(ResourceKey<? extends Registry<?>> registryKey, Identifier identifier) {
        return Component.translatable(getTranslationKey(registryKey, identifier));
    }

    /**
     * @param resourceKey the resource key
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<?> resourceKey) {
        return getTranslationKey(resourceKey.registryKey(), resourceKey.identifier());
    }

    /**
     * @param registryKey      the registry key
     * @param identifier the identifier
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<? extends Registry<?>> registryKey, Identifier identifier) {
        return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), identifier);
    }

    /**
     * Useful for attribute names from enchantment resource keys.
     *
     * @param resourceKey the resource key
     * @return the identifier
     */
    public static Identifier getIdentifier(ResourceKey<?> resourceKey) {
        return getIdentifier(resourceKey.registryKey(), resourceKey.identifier());
    }

    /**
     * Useful for attribute names from enchantment resource keys.
     *
     * @param registryKey      the registry key
     * @param identifier the identifier
     * @return the identifier
     */
    public static Identifier getIdentifier(ResourceKey<? extends Registry<?>> registryKey, Identifier identifier) {
        return identifier.withPrefix(Registries.elementsDirPath(registryKey) + ".");
    }
}
