package fuzs.puzzleslib.api.capability.v2.data;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * common wrapper for Capability (Forge) and Component (Fabric)
 *
 * @param <C> capability type
 */
public interface CapabilityKey<C extends CapabilityComponent> {

    /**
     * @return capability id
     */
    ResourceLocation getId();

    /**
     * @return capability type
     */
    Class<C> getComponentClass();

    /**
     * get capability implementation from <code>provider</code> directly
     *
     * @param provider      provider to get capability from
     * @param <V>           provider type
     * @return              capability implementation for this <code>provider</code>
     */
    @Nullable
    <V> C get(@Nullable V provider);

    /**
     * get capability implementation from <code>provider</code> as optional
     *
     * @param provider      provider to get capability from
     * @param <V>           provider type
     * @return              capability implementation for this <code>provider</code> as optional
     */
    <V> Optional<C> maybeGet(@Nullable V provider);

    /**
     * get capability implementation from <code>provider</code> directly
     * alternative to {@link #get}, will throw an exception if not present
     *
     * @param provider      provider to get capability from
     * @param <V>           provider type
     * @return              capability implementation for this <code>provider</code>
     */
    default <V> C orThrow(@Nullable V provider) {
        return this.maybeGet(provider).orElseThrow(IllegalStateException::new);
    }

    /**
     * checks if a capability is present on the given <code>provider</code>
     *
     * @param provider      provider to get capability from
     * @param <V>           provider type
     * @return              is this capability present on the given <code>provider</code>
     */
    default <V> boolean isProvidedBy(@Nullable V provider) {
        return this.get(provider) != null;
    }
}
