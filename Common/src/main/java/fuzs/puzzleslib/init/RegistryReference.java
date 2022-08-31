package fuzs.puzzleslib.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a lazy wrapper for registry object.
 * <p>Copied and adapted from <a href="https://github.com/jaredlll08/MultiLoader-Template/blob/1.18.2/examples/MultiLoader-Template-with-registration/Fabric/src/main/java/com/example/examplemod/FabricRegistrationFactory.java">FabricRegistrationFactory.java</a>
 *
 * @param <T> the type of the object
 */
public interface RegistryReference<T> {

    /**
     * Get access to the {@link Registry} of the wrapped object
     *
     * @return the registry key of the registry this reference is part of
     */
    ResourceKey<? extends Registry<? super T>> getRegistryKey();

    /**
     * Gets the {@link ResourceKey} of the registry of the object wrapped.
     *
     * @return the {@link ResourceKey} of the registry
     */
    ResourceKey<T> getResourceKey();

    /**
     * Gets the id of the object.
     *
     * @return the id of the object
     */
    ResourceLocation getResourceLocation();

    /**
     * Gets the object behind this wrapper. Calling this method too early
     * might result in crashes.
     *
     * @return the object behind this wrapper
     */
    T get();

    /**
     * Gets this object wrapped in a vanilla {@link Holder}.
     *
     * @return the holder
     */
    Holder<T> holder();

    /**
     * creates a placeholder {@link RegistryReference} for game content that needs to be registered as a mod loader specific implementation
     *
     * @param registryKey       the registry
     * @param namespace         namespace of {@link ResourceLocation}
     * @param path              path of {@link ResourceLocation}
     * @param <T>               specific object type, is inferred from variable this is stored as
     * @return                  a placeholder reference implementation
     */
    static <T> RegistryReference<T> placeholder(ResourceKey<? extends Registry<? super T>> registryKey, String namespace, String path) {
        return placeholder(registryKey, new ResourceLocation(namespace, path));
    }

    /**
     * creates a placeholder {@link RegistryReference} for game content that needs to be registered as a mod loader specific implementation
     *
     * @param registryKey       the registry
     * @param resourceLocation  object name
     * @param <T>               specific object type, is inferred from variable this is stored as
     * @return                  a placeholder reference implementation
     */
    @SuppressWarnings("unchecked")
    static <T> RegistryReference<T> placeholder(ResourceKey<? extends Registry<? super T>> registryKey, ResourceLocation resourceLocation) {
        Registry<T> registry = (Registry<T>) Registry.REGISTRY.get(registryKey.location());
        if (registry == null) {
            throw new IllegalStateException(String.format("Unable to retrieve registry from key %s", registryKey));
        }
        ResourceKey<T> resourceKey = ResourceKey.create((ResourceKey<? extends Registry<T>>) registryKey, resourceLocation);
        return new RegistryReference<>() {
            /**
             * the referenced value
             */
            private T value;

            @Override
            public ResourceKey<? extends Registry<? super T>> getRegistryKey() {
                return registryKey;
            }

            @Override
            public ResourceKey<T> getResourceKey() {
                return resourceKey;
            }

            @Override
            public ResourceLocation getResourceLocation() {
                return resourceLocation;
            }

            @Override
            public T get() {
                if (this.value == null) {
                    if (!registry.containsKey(resourceLocation)) {
                        throw new IllegalStateException(String.format("Unable to retrieve placeholder %s from registry %s", resourceLocation, registryKey));
                    }
                    this.value = registry.get(resourceLocation);
                }
                return this.value;
            }

            @Override
            public Holder<T> holder() {
                return registry.getOrCreateHolderOrThrow(this.getResourceKey());
            }
        };
    }
}
