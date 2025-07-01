package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface RegistryEntryAddedCallback<T> {

    @SuppressWarnings("unchecked")
    static <T> EventInvoker<RegistryEntryAddedCallback<T>> registryEntryAdded(ResourceKey<? extends Registry<T>> resourceKey) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        return EventInvoker.lookup((Class<RegistryEntryAddedCallback<T>>) (Class<?>) RegistryEntryAddedCallback.class,
                resourceKey);
    }

    /**
     * A callback that runs whenever a new entry is added to a {@link Registry}.
     * <p>
     * Note that the implementation is only designed for built-in registries and probably will not work with dynamic
     * registries.
     *
     * @param registry  the read-only registry
     * @param id        the resource location for the added entry
     * @param entry     the added entry
     * @param registrar access to the registry for adding additional entries
     */
    void onRegistryEntryAdded(Registry<T> registry, ResourceLocation id, T entry, BiConsumer<ResourceLocation, Supplier<T>> registrar);
}
