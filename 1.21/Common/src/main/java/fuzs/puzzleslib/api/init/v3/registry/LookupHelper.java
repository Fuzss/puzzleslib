package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;

/**
 * A few shortcuts for looking up holders in a registry from a registry access.
 */
public final class LookupHelper {

    private LookupHelper() {
        // NO-OP
    }

    /**
     * Looks up a holder in the provided registry access.
     *
     * @param entity      the registry access for retrieving the registry
     * @param registryKey the registry key
     * @param resourceKey the value key
     * @param <T>         registry value type
     * @return the holder from the registry
     */
    public static <T> Holder<T> lookup(Entity entity, ResourceKey<? extends Registry<? extends T>> registryKey, ResourceKey<T> resourceKey) {
        return lookup(entity.registryAccess(), registryKey, resourceKey);
    }

    /**
     * Looks up a holder in the provided registry access.
     *
     * @param levelReader the registry access for retrieving the registry
     * @param registryKey the registry key
     * @param resourceKey the value key
     * @param <T>         registry value type
     * @return the holder from the registry
     */
    public static <T> Holder<T> lookup(LevelReader levelReader, ResourceKey<? extends Registry<? extends T>> registryKey, ResourceKey<T> resourceKey) {
        return lookup(levelReader.registryAccess(), registryKey, resourceKey);
    }

    /**
     * Looks up a holder in the provided registry access.
     *
     * @param registries  the registry access for retrieving the registry
     * @param registryKey the registry key
     * @param resourceKey the value key
     * @param <T>         registry value type
     * @return the holder from the registry
     */
    public static <T> Holder<T> lookup(HolderLookup.Provider registries, ResourceKey<? extends Registry<? extends T>> registryKey, ResourceKey<T> resourceKey) {
        return registries.lookupOrThrow(registryKey).getOrThrow(resourceKey);
    }
}
