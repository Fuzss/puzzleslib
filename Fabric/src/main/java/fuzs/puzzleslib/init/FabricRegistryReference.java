package fuzs.puzzleslib.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * implementation for Fabric
 * @param <T> type of the referenced object
 */
public class FabricRegistryReference<T> implements RegistryReference<T> {
    /**
     * the referenced value
     */
    private final T value;
    /**
     * the resource key in {@link #registry} for {@link #value}
     */
    private final ResourceKey<T> key;
    /**
     * the {@link Registry} {@link #value} is registered to
     */
    private final Registry<? super T> registry;

    /**
     * @param value             the referenced value
     * @param resourceLocation  resource location to create {@link ResourceKey} from
     * @param registry          the {@link Registry} {@link #value} is registered to
     */
    @SuppressWarnings("unchecked")
    public FabricRegistryReference(T value, ResourceLocation resourceLocation, Registry<? super T> registry) {
        this(value, (ResourceKey<T>) ResourceKey.create(registry.key(), resourceLocation), registry);
    }

    /**
     * @param value     the referenced value
     * @param key       the resource key in {@link #registry} for {@link #value}
     * @param registry  the {@link Registry} {@link #value} is registered to
     */
    public FabricRegistryReference(T value, ResourceKey<T> key, Registry<? super T> registry) {
        this.value = value;
        this.key = key;
        this.registry = registry;
    }

    @Override
    public ResourceKey<? extends Registry<? super T>> getRegistryKey() {
        return this.registry.key();
    }

    @Override
    public ResourceKey<T> getResourceKey() {
        return this.key;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return this.key.location();
    }

    @Override
    public T get() {
        return this.value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Holder<T> holder() {
        return ((Registry<T>) this.registry).getHolderOrThrow(this.key);
    }

    @Override
    public boolean isPresent() {
        return true;
    }
}
