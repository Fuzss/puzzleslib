package fuzs.puzzleslib.capability.data;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * common wrapper for Capability (Forge) and Component (Fabric)
 * @param <T> capability type
 */
public interface CapabilityKey<T> {

    /**
     * @return capability id
     */
    ResourceLocation getId();

    /**
     * @return capability type
     */
    Class<T> getComponentClass();

    /**
     * get capability implementation from <code>provider</code> directly
     * @param provider provider to get capability from
     * @param <V>      provider type
     * @return         capability implementation for this <code>provider</code>
     */
    @Nullable
    <V> T get(@Nullable V provider);

    /**
     * get capability implementation from <code>provider</code> as optional
     * @param provider provider to get capability from
     * @param <V>      provider type
     * @return         capability implementation for this <code>provider</code> as optional
     */
    <V> Optional<T> maybeGet(@Nullable V provider);
}
