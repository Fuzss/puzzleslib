package fuzs.puzzleslib.capability;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * it does not seem possible to register capabilities in the common project, the whole structure is there,
 * the only problem is Forge's CapabilityToken which cannot be extended or wrapped or whatever
 *
 * so instead we in common we create placeholders of {@link CapabilityKey} which will need to be registered again in the mod loader specific projects
 * the placeholder will then update itself with the correct implementation when it is used
 *
 * this class shouldn't need one instance per mod, as no registering using events (which would need to be registered to a specific mod) takes place
 */
public class CapabilityController {
    /**
     * all actual capabilities implemented by mod loader specific projects
     * duplicates shouldn't be able to happen as only one mod loader implementation can run at once adding its data
     */
    private static final Map<ResourceLocation, CapabilityKey<?>> CAPABILITY_KEY_REGISTRY = Maps.newConcurrentMap();

    /**
     * called from constructors of mod loader specific {@link fuzs.puzzleslib.capability.data.CapabilityComponent} implementations
     * @param capabilityKey a proper mod loader specific implementation of {@link CapabilityKey}
     * @param <T>           capability type
     */
    public static synchronized <T> void submit(CapabilityKey<T> capabilityKey) {
        if (CAPABILITY_KEY_REGISTRY.put(capabilityKey.getId(), capabilityKey) != null) {
            throw new IllegalStateException(String.format("Duplicate capability %s", capabilityKey.getId()));
        }
    }

    /**
     * creates a placeholder key which will update itself when used for the first time
     * @param modId          mod namespace
     * @param keyId          capability key id
     * @param componentClass capability type
     * @param <T> capability type
     * @return the placeholder key
     */
    public static <T> CapabilityKey<T> makeCapabilityKey(String modId, String keyId, Class<T> componentClass) {
        return makeCapabilityKey(new ResourceLocation(modId, keyId), componentClass);
    }

    /**
     * creates a placeholder key which will update itself when used for the first time
     * @param id capability id
     * @param componentClass capability type
     * @param <T> capability type
     * @return the placeholder key
     */
    public static <T> CapabilityKey<T> makeCapabilityKey(ResourceLocation id, Class<T> componentClass) {
        return new CapabilityKey<>() {
            private CapabilityKey<T> holder;

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public Class<T> getComponentClass() {
                return componentClass;
            }

            @Override
            public <V> @Nullable T get(@Nullable V provider) {
                this.validateHolder();
                return this.holder.get(provider);
            }

            @Override
            public <V> Optional<T> maybeGet(@Nullable V provider) {
                this.validateHolder();
                return this.holder.maybeGet(provider);
            }

            @SuppressWarnings("unchecked")
            private void validateHolder() {
                if (this.holder == null) {
                    if (CAPABILITY_KEY_REGISTRY.containsKey(id)) {
                        CapabilityKey<?> capabilityKey = CAPABILITY_KEY_REGISTRY.get(id);
                        if (capabilityKey.getComponentClass() == componentClass) {
                            this.holder = (CapabilityKey<T>) capabilityKey;
                            return;
                        }
                    }
                    throw new IllegalStateException(String.format("No valid capability implementation registered for %s", id));
                }
            }
        };
    }
}
