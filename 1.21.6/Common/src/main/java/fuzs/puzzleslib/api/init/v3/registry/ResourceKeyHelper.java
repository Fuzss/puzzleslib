package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

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
     * @param resourceLocation the resource location
     * @return the component
     */
    public static MutableComponent getComponent(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return Component.translatable(getTranslationKey(registryKey, resourceLocation));
    }

    /**
     * @param resourceKey the resource key
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<?> resourceKey) {
        return getTranslationKey(resourceKey.registryKey(), resourceKey.location());
    }

    /**
     * @param registryKey      the registry key
     * @param resourceLocation the resource location
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), resourceLocation);
    }

    /**
     * Useful for attribute names from enchantment resource keys.
     *
     * @param resourceKey the resource key
     * @return the resource location
     */
    public static ResourceLocation getResourceLocation(ResourceKey<?> resourceKey) {
        return getResourceLocation(resourceKey.registryKey(), resourceKey.location());
    }

    /**
     * Useful for attribute names from enchantment resource keys.
     *
     * @param registryKey      the registry key
     * @param resourceLocation the resource location
     * @return the resource location
     */
    public static ResourceLocation getResourceLocation(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(Registries.elementsDirPath(registryKey) + ".");
    }
}
