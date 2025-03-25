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
     * @param resourceKey the resource key
     * @return the component
     */
    public static MutableComponent getComponent(ResourceKey<?> resourceKey) {
        return Component.translatable(getTranslationKey(resourceKey));
    }

    /**
     * @param resourceKey the resource key
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<?> resourceKey) {
        return Util.makeDescriptionId(resourceKey.registry().getPath(), resourceKey.location());
    }

    /**
     * @param registryKey      the registry key
     * @param resourceLocation the resource location
     * @return the component
     */
    public static MutableComponent getComponent(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return Component.translatable(getTranslationKey(registryKey, resourceLocation));
    }

    /**
     * @param registryKey      the registry key
     * @param resourceLocation the resource location
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), resourceLocation);
    }
}
