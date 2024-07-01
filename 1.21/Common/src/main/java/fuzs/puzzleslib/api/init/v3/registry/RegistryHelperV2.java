package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * A helper class for Minecraft's registries.
 */
public final class RegistryHelperV2 {

    private RegistryHelperV2() {

    }

    /**
     * Finds a registry for the provided {@link ResourceKey} from searching in {@link BuiltInRegistries#REGISTRY}.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    public static <T> Registry<T> findBuiltInRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        Registry<T> registry = findNullableBuiltInRegistry(registryKey);
        Objects.requireNonNull(registry, "registry for %s is null".formatted(registryKey));
        return registry;
    }

    /**
     * Finds a registry for the provided {@link ResourceKey} from searching in {@link BuiltInRegistries#REGISTRY}.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> Registry<T> findNullableBuiltInRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).get((ResourceKey<Registry<T>>) registryKey);
    }

    /**
     * Finds a registry for the provided {@link ResourceKey}.
     * <p>
     * Dynamic registries are supported while a game server is already running.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    public static <T> Registry<T> findGameRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        Registry<T> registry = findNullableGameRegistry(registryKey);
        Objects.requireNonNull(registry, "registry for %s is null".formatted(registryKey));
        return registry;
    }

    /**
     * Finds a registry for the provided {@link ResourceKey}.
     * <p>
     * Dynamic registries are supported while a game server is already running.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    @SuppressWarnings("unchecked")
    public static <T> Registry<T> findNullableGameRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Optional<Registry<T>> registry = Optional.empty();
        MinecraftServer minecraftServer = CommonAbstractions.INSTANCE.getMinecraftServer();
        if (minecraftServer != null) {
            registry = minecraftServer.registryAccess().registry((ResourceKey<Registry<T>>) registryKey);
        }
        if (registry.isEmpty()) {
            registry = Optional.ofNullable(findNullableBuiltInRegistry(registryKey));
        }

        return registry.orElse(null);
    }

    /**
     * Retrieves the {@link ResourceKey} for a game object from a {@link Registry}.
     *
     * @param registryKey the registry key
     * @param object      the game object to retrieve
     * @param <T>         registry and game object type
     * @return the resource key for this game object
     */
    public static <T> Optional<ResourceKey<T>> getResourceKey(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
        return getHolderReference(registryKey, object).map(Holder.Reference::key);
    }

    /**
     * Retrieves the {@link ResourceKey} for a game object from a {@link Registry}.
     *
     * @param registry the registry
     * @param object   the game object to retrieve
     * @param <T>      registry and game object type
     * @return the resource key for this game object
     */
    public static <T> Optional<ResourceKey<T>> getResourceKey(Registry<T> registry, T object) {
        return getHolderReference(registry, object).map(Holder.Reference::key);
    }

    /**
     * Retrieves the {@link ResourceKey} for a game object from a {@link Registry}.
     *
     * @param registryKey the registry key
     * @param object      the game object to retrieve
     * @param <T>         registry and game object type
     * @return the resource key for this game object
     */
    public static <T> ResourceKey<T> getResourceKeyOrThrow(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
        return getResourceKey(registryKey, object).orElseThrow(() -> {
            return new IllegalStateException("Missing object in " + registryKey + ": " + object);
        });
    }

    /**
     * Retrieves the {@link ResourceKey} for a game object from a {@link Registry}.
     *
     * @param registry the registry
     * @param object   the game object to retrieve
     * @param <T>      registry and game object type
     * @return the resource key for this game object
     */
    public static <T> ResourceKey<T> getResourceKeyOrThrow(Registry<T> registry, T object) {
        return getResourceKey(registry, object).orElseThrow(() -> {
            return new IllegalStateException("Missing object in " + registry.key() + ": " + object);
        });
    }

    /**
     * Retrieves the {@link Holder.Reference} for a game object from a {@link Registry}.
     *
     * @param registryKey the registry key
     * @param object      the game object to retrieve
     * @param <T>         registry and game object type
     * @return the holder reference for this game object
     */
    public static <T> Optional<Holder.Reference<T>> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
        return Optional.ofNullable(getBuiltInRegistryHolder(object)).or(() -> {
            Registry<T> registry = findGameRegistry(registryKey);
            return registry.getResourceKey(object).flatMap(registry::getHolder);
        });
    }

    /**
     * Retrieves the {@link Holder.Reference} for a game object from a {@link Registry}.
     *
     * @param registry the registry
     * @param object   the game object to retrieve
     * @param <T>      registry and game object type
     * @return the holder reference for this game object
     */
    public static <T> Optional<Holder.Reference<T>> getHolderReference(Registry<T> registry, T object) {
        return Optional.ofNullable(getBuiltInRegistryHolder(object)).or(() -> {
            return registry.getResourceKey(object).flatMap(registry::getHolder);
        });
    }

    /**
     * Retrieves the {@link Holder.Reference} for a game object from a {@link Registry}.
     *
     * @param registryKey the registry key
     * @param object      the game object to retrieve
     * @param <T>         registry and game object type
     * @return the holder reference for this game object
     */
    public static <T> Holder.Reference<T> getHolderOrThrow(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
        return getHolderReference(registryKey, object).orElseThrow(() -> {
            return new IllegalStateException("Missing object in " + registryKey + ": " + object);
        });
    }

    /**
     * Retrieves the {@link Holder.Reference} for a game object from a {@link Registry}.
     *
     * @param registry the registry
     * @param object   the game object to retrieve
     * @param <T>      registry and game object type
     * @return the holder reference for this game object
     */
    public static <T> Holder.Reference<T> getHolderOrThrow(Registry<T> registry, T object) {
        return getHolderReference(registry, object).orElseThrow(() -> {
            return new IllegalStateException("Missing object in " + registry.key() + ": " + object);
        });
    }

    /**
     * Retrieves the {@link Holder} for a game object from a {@link Registry}.
     *
     * @param registryKey the registry key
     * @param object      the game object to retrieve
     * @param <T>         registry and game object type
     * @return the holder for this game object
     */
    public static <T> Holder<T> wrapAsHolder(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
        return findGameRegistry(registryKey).wrapAsHolder(object);
    }

    /**
     * Checks if a certain tag contains a given value.
     *
     * @param tagKey the tag key
     * @param object the game object to check the tag key for
     * @param <T>    tag and game object type
     * @return is the game object contained in the tag
     */
    public static <T> boolean is(TagKey<T> tagKey, T object) {
        Holder.Reference<T> holder = getBuiltInRegistryHolder(object);
        if (holder != null) {
            return holder.is(tagKey);
        } else {
            Registry<T> registry = findGameRegistry(tagKey.registry());
            return tagKey.isFor(registry.key()) && registry.wrapAsHolder(object).is(tagKey);
        }
    }

    /**
     * Get the built-in registry holder from a game object if available.
     *
     * @param object the game object to get the built-in registry holder from
     * @param <T>    game object type
     * @return the built-in holder for this game object if available
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    @Nullable
    public static <T> Holder.Reference<T> getBuiltInRegistryHolder(T object) {
        Holder.Reference<?> holder = null;
        if (object instanceof Block block) {
            holder = block.builtInRegistryHolder();
        } else if (object instanceof Item item) {
            holder = item.builtInRegistryHolder();
        } else if (object instanceof EntityType<?> entityType) {
            holder = entityType.builtInRegistryHolder();
        } else if (object instanceof Fluid fluid) {
            holder = fluid.builtInRegistryHolder();
        } else if (object instanceof BlockEntityType<?> blockEntityType) {
            holder = blockEntityType.builtInRegistryHolder();
        }

        return (Holder.Reference<T>) holder;
    }
}
