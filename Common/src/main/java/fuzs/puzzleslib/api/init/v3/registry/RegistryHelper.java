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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.Optional;

/**
 * A helper class for Minecraft's registries.
 */
public final class RegistryHelper {

    private RegistryHelper() {

    }

    /**
     * Finds a registry for the provided {@link ResourceKey},
     * with dynamic registries only being supported when a game server is already running.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    public static <T> Registry<T> findRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        return findRegistry(registryKey, false);
    }

    /**
     * Finds a built-in registry for the provided {@link ResourceKey}.
     *
     * @param registryKey the registry key
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    public static <T> Registry<T> findBuiltInRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        return findRegistry(registryKey, true);
    }

    /**
     * Finds a registry for the provided {@link ResourceKey},
     * with dynamic registries only being supported when a game server is already running.
     *
     * @param registryKey the registry key
     * @param onlyBuiltIn only search in {@link BuiltInRegistries#REGISTRY}
     * @param <T>         registry value type
     * @return the corresponding registry
     */
    @SuppressWarnings("unchecked")
    private static <T> Registry<T> findRegistry(ResourceKey<? extends Registry<? super T>> registryKey, boolean onlyBuiltIn) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Optional<Registry<T>> registry = Optional.empty();
        if (!onlyBuiltIn) {
            MinecraftServer minecraftServer = CommonAbstractions.INSTANCE.getMinecraftServer();
            if (minecraftServer != null) {
                registry = minecraftServer.registryAccess().registry((ResourceKey<Registry<T>>) registryKey);
            }
        }
        if (registry.isEmpty()) {
            registry = ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).getOptional((ResourceKey<Registry<T>>) registryKey);
        }
        return registry.orElseThrow(() -> {
            return new IllegalArgumentException("Registry for key %s not found".formatted(registryKey));
        });
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
        return findRegistry(registryKey).getResourceKey(object);
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
     * Retrieves the {@link Holder.Reference} for a game object from a {@link Registry}.
     *
     * @param registryKey the registry key
     * @param object      the game object to retrieve
     * @param <T>         registry and game object type
     * @return the holder reference for this game object
     */
    public static <T> Optional<Holder.Reference<T>> getHolder(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
        Registry<T> registry = findRegistry(registryKey);
        return registry.getResourceKey(object).flatMap(registry::getHolder);
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
        return getHolder(registryKey, object).orElseThrow(() -> {
            return new IllegalStateException("Missing object in " + registryKey + ": " + object);
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
        return findRegistry(registryKey).wrapAsHolder(object);
    }

    /**
     * Checks if a certain tag contains a given value.
     * <p>Some game objects such as blocks, items and entity types do not require this approach as they hold a reference to their registry holder.
     *
     * @param tagKey the tag key
     * @param object the game object to check the tag key for
     * @param <T>    tag and game object type
     * @return is the game object contained in the tag
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public static <T> boolean is(TagKey<T> tagKey, T object) {
        if (object instanceof Block block) {
            return block.builtInRegistryHolder().is((TagKey<Block>) tagKey);
        } else if (object instanceof Item item) {
            return item.builtInRegistryHolder().is((TagKey<Item>) tagKey);
        } else if (object instanceof EntityType<?> entityType) {
            return entityType.builtInRegistryHolder().is((TagKey<EntityType<?>>) tagKey);
        } else if (object instanceof GameEvent gameEvent) {
            return gameEvent.builtInRegistryHolder().is((TagKey<GameEvent>) tagKey);
        } else if (object instanceof Fluid fluid) {
            return fluid.builtInRegistryHolder().is((TagKey<Fluid>) tagKey);
        }
        Registry<T> registry = findRegistry(tagKey.registry());
        return tagKey.isFor(registry.key()) && registry.wrapAsHolder(object).is(tagKey);
    }
}
