package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.resources.ResourceLocation;

/**
 * Common wrapper for Fabric's Component / NeoForge's AttachmentType / Forge's Capability.
 *
 * @param <T> capability component holder type
 * @param <C> capability component type
 */
public interface CapabilityKey<T, C extends CapabilityComponent<T>> {

    /**
     * Identifier for this capability used for synchronization and serialization.
     *
     * @return capability id
     */
    ResourceLocation identifier();

    /**
     * Get capability implementation from a holder.
     *
     * @param holder provider to get capability from
     * @return capability implementation for given holder
     */
    C get(T holder);

    /**
     * Checks if a capability is compatible with a holder.
     *
     * @param holder provider to test
     * @return is this holder compatible
     */
    boolean isProvidedBy(Object holder);

    /**
     * Called from {@link CapabilityComponent#setChanged()} to notify that the component has changed and needs to be serialized / synchronized.
     *
     * <p>Actual behavior varies depending on the type of holder, for that see implementations in sub-interfaces.
     *
     * @param capabilityComponent the component that has changed
     */
    void setChanged(C capabilityComponent);
}
