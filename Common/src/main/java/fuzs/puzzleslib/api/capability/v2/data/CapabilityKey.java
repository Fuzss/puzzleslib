package fuzs.puzzleslib.api.capability.v2.data;

import net.minecraft.resources.ResourceLocation;

/**
 * Common wrapper for Component / AttachmentType / Capability.
 *
 * @param <C> capability type
 */
public interface CapabilityKey<T, C extends CapabilityComponent<T>> {

    /**
     * @return capability id
     */
    ResourceLocation identifier();

    /**
     * Get capability implementation directly from a <code>holder</code>.
     *
     * @param holder provider to get capability from
     * @return capability implementation for this <code>holder</code>
     */
    C get(T holder);

    /**
     * checks if a capability is present on the given <code>holder</code>
     *
     * @param holder provider to get capability from
     * @return is this capability present on the given <code>holder</code>
     */
    boolean isProvidedBy(Object holder);

    void setChanged(C capabilityComponent);
}
