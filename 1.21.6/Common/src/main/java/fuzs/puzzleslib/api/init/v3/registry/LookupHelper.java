package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;

import java.util.Objects;
import java.util.Optional;

/**
 * A few shortcuts for looking up holders in a registry from a registry access.
 */
public final class LookupHelper {

    private LookupHelper() {
        // NO-OP
    }

    /**
     * Finds a registry for the provided {@link ResourceKey} in {@link BuiltInRegistries#REGISTRY}.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the registry
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Registry<T>> getRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).getOptional((ResourceKey<Registry<T>>) registryKey);
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
        Objects.requireNonNull(entity, "entity is null");
        return lookup(entity.registryAccess(), registryKey, resourceKey);
    }

    /**
     * Looks up a holder in the provided registry access.
     *
     * @param level       the registry access for retrieving the registry
     * @param registryKey the registry key
     * @param resourceKey the value key
     * @param <T>         registry value type
     * @return the holder from the registry
     */
    public static <T> Holder<T> lookup(LevelReader level, ResourceKey<? extends Registry<? extends T>> registryKey, ResourceKey<T> resourceKey) {
        Objects.requireNonNull(level, "level is null");
        return lookup(level.registryAccess(), registryKey, resourceKey);
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
        Objects.requireNonNull(registries, "registries is null");
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(resourceKey, "resource key is null");
        return registries.lookupOrThrow(registryKey).getOrThrow(resourceKey);
    }
}
